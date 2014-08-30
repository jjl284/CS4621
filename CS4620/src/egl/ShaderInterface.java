package egl;

import java.util.HashMap;

public class ShaderInterface {
	public final ArrayBind[] Binds;

    public ShaderInterface(ArrayBind[] binds) {
        Binds = new ArrayBind[binds.length];
        for(int i = 0;i < Binds.length;i++) {
        	Binds[i] = new ArrayBind(
    			binds[i].Semantic,
    			binds[i].CompType,
    			binds[i].CompCount,
    			binds[i].Offset,
    			binds[i].Normalized
    			);
        }
    }

    public int Build(HashMap<Integer, Integer> dSemBinds) {
        int bound = 0;
        for(int i = 0; i < Binds.length; i++) {
        	Integer v = dSemBinds.get(Binds[i].Semantic);
        	Binds[i].Location = v == null ? -1 : v;
        	if(Binds[i].Location >= 0) bound++;
        }
        return bound;
    }
}
