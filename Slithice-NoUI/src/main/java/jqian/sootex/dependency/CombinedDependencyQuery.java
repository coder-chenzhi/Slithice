package jqian.sootex.dependency;

import java.util.Collection;

import soot.SootMethod;
import soot.Unit;
import soot.jimple.toolkits.callgraph.ReachableMethods;

/**
 *
 */
public class CombinedDependencyQuery implements IDependencyQuery {
	private IDependencyQuery _left;
	private IDependencyQuery _right;
	private ReachableMethods _forLeft;
	
	public CombinedDependencyQuery(IDependencyQuery left, IDependencyQuery right, ReachableMethods forLeft){
		this._left = left;
		this._right = right;
		this._forLeft = forLeft;
    }

	public Collection<Unit> getCtrlDependencies(SootMethod m, Unit u) {
		if(_forLeft.contains(m)){
			return _left.getCtrlDependencies(m, u);
		}
		else{
			return _right.getCtrlDependencies(m, u);
		}
	}

	public Collection<Unit> getWRDependencies(SootMethod m, Unit u) {
		if(_forLeft.contains(m)){
			return _left.getWRDependencies(m, u);
		}
		else{
			return _right.getWRDependencies(m, u);
		}
	}

	public Collection<Unit> getRWDependencies(SootMethod m, Unit u) {
		if(_forLeft.contains(m)){
			return _left.getRWDependencies(m, u);
		}
		else{
			return _right.getRWDependencies(m, u);
		}
	}

	public Collection<Unit> getWWDependencies(SootMethod m, Unit u) {
		if(_forLeft.contains(m)){
			return _left.getWWDependencies(m, u);
		}
		else{
			return _right.getWWDependencies(m, u);
		}
	}

	public void release(SootMethod m) {
		_left.release(m);
		_right.release(m);
	}

}
