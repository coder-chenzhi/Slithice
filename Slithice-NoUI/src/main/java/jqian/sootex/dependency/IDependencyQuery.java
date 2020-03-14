package jqian.sootex.dependency;

import java.util.Collection;

import soot.SootMethod;
import soot.Unit;

public interface IDependencyQuery {
	public Collection<Unit> getCtrlDependencies(SootMethod m, Unit u);
	
	/** Get Write->Read dependences. */
	public Collection<Unit> getWRDependencies(SootMethod m, Unit u);
	
	/** Get Read->Write dependences. */
	public Collection<Unit> getRWDependencies(SootMethod m, Unit u);
	
	/** Get Write->Write dependences. */
	public Collection<Unit> getWWDependencies(SootMethod m, Unit u);
	
	public void release(SootMethod m);
}
