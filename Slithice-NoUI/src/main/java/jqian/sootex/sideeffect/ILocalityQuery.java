package jqian.sootex.sideeffect;

import soot.Local;
import soot.SootMethod;

public interface ILocalityQuery {
	/**
	 * if v is local for m, which means the lifecycle of v is same with the lifecycle of m.
	 * In other word, v is created inside m and is released when m returns.
	 * @param m
	 * @param v
	 * @return
	 */
	public boolean isRefTgtLocal(SootMethod m, Local v);
	public boolean isRefTgtFresh(SootMethod m, Local v);
}
