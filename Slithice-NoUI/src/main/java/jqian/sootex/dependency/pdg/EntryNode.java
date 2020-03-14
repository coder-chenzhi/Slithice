package jqian.sootex.dependency.pdg;

import jqian.sootex.util.CFGEntry;
import soot.*;


/**
 * Model the entry node of a PDG. 
 */
public class EntryNode extends DependenceNode{
   public EntryNode(MethodOrMethodContext mc){
       super(mc);
   }
   
   public Object clone(){
       return new EntryNode(_mc);
   }

    /**
     * Get the corresponding method
     * @return
     */
   public SootMethod getMethod(){
       return _mc.method();
   }
   
   public Object getBinding(){
	   return CFGEntry.v();   
   }
   
   public String toString(){
       SootMethod m = _mc.method();
	   StringBuilder out = new StringBuilder();
	   out.append("#").append(_id).append(" ENTRY ").
               append(m.getDeclaringClass().getShortName()).
               append(".").append(m.getName());
	   return out.toString();
   }   
}
