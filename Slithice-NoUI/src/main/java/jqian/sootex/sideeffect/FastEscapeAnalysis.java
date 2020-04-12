/** 
 *  
 * 
 * 	@author Ju Qian {jqian@live.com}
 * 	@date 2011-8-8
 * 	@version
 */
package jqian.sootex.sideeffect;

import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import soot.Body;
import soot.Local;
import soot.RefLikeType;
import soot.SootMethod;
import soot.Unit;
import soot.Value;
import soot.jimple.*;
import soot.jimple.toolkits.callgraph.CallGraph;
import soot.shimple.PhiExpr;
import soot.toolkits.graph.MutableDirectedGraph;

import jqian.Global;
import jqian.sootex.Cache;
import jqian.sootex.util.SootUtils;
import jqian.sootex.util.callgraph.Callees;
import jqian.sootex.util.graph.GraphHelper;
import jqian.util.Utils;

/**
 * Implement the fresh method and variable analysis provided by Gay@CC 2000 A
 * fresh method return a newly created objects (not exist before method call).
 * NOTE that the object may already bean escaped or also return from parameters.
 * 
 */
@SuppressWarnings({ "unchecked", "rawtypes" })
public class FastEscapeAnalysis extends FreshAnalysis {
	// 通过栈上的传递，经由返回值漏出的变量
	/**
	 * 2020.2.6 Chenzhi
	 * returned Locals of methods
	 */
	protected Set<Local>[] _returned;

	// 从内部经throw、堆属性域、静态属性域漏出的变量
	/**
	 * 2020.2.6 Chenzhi
	 * escaped Locals of methods
	 */
	protected Set<Local>[] _vescaped;
	/**
	 * 2020.2.6 Chenzhi
	 * indicate whether any return values of method has been escaped
	 */
	protected boolean[] _mescaped;

	public FastEscapeAnalysis(CallGraph cg) {
		super(cg);
	}

	/**
	 * Add constrains caused by dependencies between out to in parameters. More specifically,
	 * there are some callee whose return value has data-dependency on its actual parameters.
	 * Therefore, the return value of the caller has data-dependency on its formal parameters.
	 * @param varConn
	 * @param tgt
	 * @param returned
	 * @param left
	 * @param right
	 */
	private void addParamInOutConstraints(MutableDirectedGraph<Object> varConn,
			SootMethod tgt, Set<Local> returned, Value left, Value right) {
		if (returned.isEmpty()) {
			return;
		}

		Body body = tgt.retrieveActiveBody();
		if (!tgt.isStatic()) {
			Local thiz = body.getThisLocal();
			if (returned.contains(thiz)) {
				varConn.addEdge(left, ((InstanceInvokeExpr) right).getBase());
			}
		}
		int pc = tgt.getParameterCount();
		InvokeExpr invoke = (InvokeExpr) right;
		for (int i = 0; i < pc; i++) {
			Local p = body.getParameterLocal(i);
			if (returned.contains(p)) {
				Value lv = invoke.getArg(i);
				if (lv instanceof Local) {
					varConn.addEdge(left, lv);
				}
			}
		}
	}

	/**
	 * return不是只要分析ReturnStmt，获取一下被返回的Value就可以了么？为什么要分析DefinitionStmt的情况
	 * 2020.2.5 Chenzhi we need to analyze the transitive returned value
	 * @param m
	 */
	protected void returnedAnalysis(SootMethod m) {
		// build a variable connection graph
		MutableDirectedGraph<Object> varConn = initVarConnectionGraph(m);
		Body body = m.retrieveActiveBody();
		
		for (Unit u : body.getUnits()) {
			if (u instanceof ReturnStmt) {
				Value v = ((ReturnStmt) u).getOp();
				if (v.getType() instanceof RefLikeType && v instanceof Local) {
					varConn.addEdge(Boolean.TRUE, v);
				}
			}  else if (u instanceof DefinitionStmt) {
				DefinitionStmt d = (DefinitionStmt) u;
				Value left = d.getLeftOp();
				Value right = d.getRightOp();

				// assignment on non-reference variables/**
				if (!(left.getType() instanceof RefLikeType)) {
					// do nothing
				} else if (left instanceof Local) {
					// Chenzhi
					// why we don't handle those cases, such as l = r.f, l = r[], l = g,
					// like what escape analysis and fresh analysis do
					// Because we don't need to verify whether those locals will be returned,
					// as those locals have escaped already. The result of isRefTgtLocal(SootMethod m, Local v) is fixed.
					if (right instanceof Local) {
						varConn.addEdge(left, right);
					} else if (right instanceof CastExpr) {
						CastExpr cast = (CastExpr) right;
						Value op = cast.getOp();
						if (op instanceof Local) {
							varConn.addEdge(left, op);
						}// 2012.2.15 tao
					} else if (right instanceof PhiExpr) {
						List<Value> arg=((PhiExpr) right).getValues();
						
						for(Value value:arg){
							varConn.addEdge(left, value);
						}
						//throw new RuntimeException("PhiExpr processing has not implemented");
					} else if (right instanceof InvokeExpr) {
						Callees callees = new Callees(_cg, u);
						for (SootMethod tgt : callees.explicits()) {
							Set<Local> returned = _returned[tgt.getNumber()];
							// if called method not analyzed (recursion), use worst assumption
							// 2020.2.5 Chenzhi
							// We assume left depends on all parameters of right.
							// This branch should not happen ideally because we traverse method with
							// topological order and the callee methods should be visited before
							// the caller methods. However, there are some special cases ,
							// such as recursion, will lead to this branch.
							if (returned == null) {
								// what if tgt is static?
								if (!tgt.isStatic()) {
									varConn.addEdge(left, ((InstanceInvokeExpr) right).getBase());
								}
								int pc = tgt.getParameterCount();
								InvokeExpr invoke = (InvokeExpr) right;
								for (int i = 0; i < pc; i++) {
									Value lv = invoke.getArg(i);
									if (lv instanceof Local	&& lv.getType() instanceof RefLikeType) {
										varConn.addEdge(left, lv);
									}
								}
								break; // break here as we use the worst case
							} else {
								addParamInOutConstraints(varConn, tgt,
										returned, left, right);
							}
						}
					}
				}
			}
		}

		// solve the constraints
		Set returned = GraphHelper.getReachables(varConn, Boolean.TRUE);
		_returned[m.getNumber()] = returned;
		
	}

	private void handleMethodCallForEscape(	MutableDirectedGraph<Object> varConn, 
			Value left, Value right, Unit call) {
		Callees callees = new Callees(_cg, call);
		for (SootMethod tgt : callees.explicits()) {
			int tgtId = tgt.getNumber();
			Set<Local> escaped = _vescaped[tgtId];
			// if callee not analyzed yet, use worst assumptions
			// 2020.2.6 Chenzhi
			// We assume all are escaped
			if (escaped == null) {
				// return value itself
				if (left != null) {
					varConn.addEdge(Boolean.TRUE, left);
				}

				// receiver
				if (!tgt.isStatic()) {
					varConn.addEdge(Boolean.TRUE,((InstanceInvokeExpr) right).getBase());
				}

				// arguments
				int pc = tgt.getParameterCount();
				InvokeExpr invoke = (InvokeExpr) right;
				for (int i = 0; i < pc; i++) {
					Value lv = invoke.getArg(i);
					if (lv instanceof Local	&& lv.getType() instanceof RefLikeType) {
						varConn.addEdge(Boolean.TRUE, lv);
					}
				}
				break;
			} else {
				// if returned value escapes, then the source will also escapes
				if (left != null) {
					Set<Local> returned = _returned[tgtId];
					addParamInOutConstraints(varConn, tgt, returned, left, right);
				}

				// return value already escaped in the callee
				if (left != null && _mescaped[tgtId]) {
					varConn.addEdge(Boolean.TRUE, left);
				}

				// receiver value already escaped in the callee
				Body body = tgt.retrieveActiveBody();
				if (!tgt.isStatic()) {
					Local thiz = body.getThisLocal();
					if (escaped.contains(thiz)) {
						varConn.addEdge(Boolean.TRUE,
								((InstanceInvokeExpr) right).getBase());
					}
				}

				// arguments
				int pc = tgt.getParameterCount();
				InvokeExpr invoke = (InvokeExpr) right;
				for (int i = 0; i < pc; i++) {
					Local p = body.getParameterLocal(i);
					if (escaped.contains(p)) {
						Value lv = invoke.getArg(i);
						if (lv instanceof Local
								&& lv.getType() instanceof RefLikeType) {
							varConn.addEdge(Boolean.TRUE, lv);
						}
					}
				}
			}
		}

		// TODO: Ignore implicit method calls here
	}

	protected void escapedAnalysis(SootMethod m) {
		Set<Local> returns = new HashSet<Local>();

		// build a constraint graph
		MutableDirectedGraph<Object> varConn = initVarConnectionGraph(m);
		Body body = m.retrieveActiveBody();
		
		for (Unit u : body.getUnits()) {
			if (u instanceof ReturnStmt) {
				Value v = ((ReturnStmt) u).getOp();
				if (v.getType() instanceof RefLikeType && v instanceof Local) {
					returns.add((Local) v);
				}
			}  else if (u instanceof ThrowStmt) {
				Value t = ((ThrowStmt) u).getOp();
				varConn.addEdge(Boolean.TRUE, t);
			} else if (u instanceof DefinitionStmt) {
				DefinitionStmt d = (DefinitionStmt) u;
				Value left = d.getLeftOp();
				Value right = d.getRightOp();

				if (!(left.getType() instanceof RefLikeType)) {
					// assignment on non-reference variables, do nothing
				} else if (left instanceof Local) {
					// 1. l = @param, l = @this
					if (d instanceof IdentityStmt) {
					}
					// 2. "l = new C", "l = constant"
					else if (right instanceof AnyNewExpr
							|| right instanceof Constant) {
					}
					// 3. l = r
					else if (right instanceof Local) {
						varConn.addEdge(left, right);
					}
					// 4. l = (cast)r
					else if (right instanceof CastExpr) {
						CastExpr cast = (CastExpr) right;
						Value op = cast.getOp();
						if (op instanceof Local) {
							varConn.addEdge(left, op);
						}
					}
					// 5. l = r.f, l = r[], l = g
					else if (right instanceof ConcreteRef) {
						// The escape status of l or r will not be changed by those operations
					} else if (right instanceof InvokeExpr) {
						handleMethodCallForEscape(varConn, left, right, u);
					}
					// phiExpr
					else if (right instanceof PhiExpr) {
						List<Value> arg=((PhiExpr) right).getValues();
					
						for(Value value:arg){
							varConn.addEdge(left, value);
						}
						//throw new RuntimeException("PhiExpr processing has not implemented");
					} 
					else {
						// 2012.2.11 tao modified
						// throw new RuntimeException();
					}
				}
				// 6. g = r, l.f = r, l[] = r 
				else if (left instanceof ConcreteRef) {
					// 2020.2.6 Chenzhi
					// Actually, instanceFieldRef may not escape, but tracking the usage of field is
					// costly. We want to keep our escape analysis as fast as possible. Moreover,
					// this way, assuming all field access will escape, reduces the precision of our analysis,
					// but increases the safeness of our analysis.
					// 2020.4.9 FIXME l.f = r may not escape
					if (right instanceof Local) {
						if (left instanceof StaticFieldRef) {
							varConn.addEdge(Boolean.TRUE, right);
						} else if (left instanceof InstanceFieldRef) {
							varConn.addEdge(((InstanceFieldRef) left).getBase(), right);
						} else if (left instanceof ArrayRef) {
							varConn.addEdge(((ArrayRef) left).getBase(), right);
						}
					}
				}
				// others
				else {
					throw new RuntimeException();
				}
			} else if (u instanceof InvokeStmt) {
				handleMethodCallForEscape(varConn, null, ((InvokeStmt) u).getInvokeExpr(), u);
			}
		}

		// solve constraints
		Set escaped = GraphHelper.getReachables(varConn, Boolean.TRUE);
		_vescaped[m.getNumber()] = escaped;
		boolean esc = false;
		for (Local r : returns) {
			if (escaped.contains(r)) {
				esc = true;
				break;
			}
		}
		_mescaped[m.getNumber()] = esc;
	}

	public void build() {
		Date startTime = new Date();

		_mfresh = new boolean[SootUtils.getMethodCount()];
		_vnfresh = new Set[SootUtils.getMethodCount()];
		_returned = new Set[SootUtils.getMethodCount()];
		_vescaped = new Set[SootUtils.getMethodCount()];
		_mescaped = new boolean[SootUtils.getMethodCount()];

		List<?> rm = Cache.v().getTopologicalOrder();
		for (Iterator<?> it = rm.iterator(); it.hasNext();) {
			SootMethod m = (SootMethod) it.next();
			if (m.isConcrete()) {
				System.out.println("[DEBUG] " + m.getSignature() + " : " + m.getNumber());
				boolean hasRefReturn = m.getReturnType() instanceof RefLikeType;
				if (hasRefReturn) {
					returnedAnalysis(m);
				} else {
					_returned[m.getNumber()] = Collections.EMPTY_SET;
				}
				escapedAnalysis(m);

				freshAnalysis(m);
			}
		}

		_cg = null;
		Date endTime = new Date();
		Global.v().out.println("[FastEscape] " + rm.size()
				+ " methods analyszed in " + Utils.getTimeConsumed(startTime, endTime));
	}

	public Set<Local> getReturnedLocals(SootMethod m) {
		return _returned[m.getNumber()];
	}

	public Set<Local> getEscapedLocals(SootMethod m) {
		return _vescaped[m.getNumber()];
	}

	public boolean isReturnValueEscaped(SootMethod m) {
		return _mescaped[m.getNumber()];
	}

	/**
	 * if variable v in method m is local
	 * @param m method
	 * @param v variable
	 * @return
	 */
	public boolean isRefTgtLocal(SootMethod m, Local v) {
		int mId = m.getNumber();
		Set<Local> escaped = _vescaped[mId];
		Set<Local> returned = _returned[mId];
		Set<Local> nonfresh = _vnfresh[mId];

		// if v escape from m
		if (escaped == null || escaped.contains(v)) {
			return false;
		}
		// if v return from m
		if (returned == null || returned.contains(v)) {
			return false;
		}
		// if v is non-fresh variable
		if (nonfresh.contains(v)) {
			return false;
		}

		return true;
	}

	/**
	 * get all local variables in method
	 * @param m
	 * @return
	 */
	public Set<Local> getLocalVars(SootMethod m) {
		Set<Local> localVars = new HashSet<>();
		for (Local l : m.getActiveBody().getLocals()) {
			if (isRefTgtLocal(m, l)) {
				localVars.add(l);
			}
		}
		return localVars;
	}
}
