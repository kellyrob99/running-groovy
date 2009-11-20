import groovy.lang.Binding;
import groovy.lang.Script;

import java.util.List;

public class TestJavaTwo extends Script
{
    public Object run()
    {
        Binding binding1 = getBinding();
        List args = (List) binding1.getVariable("args");
        binding1.setVariable("myArgs", args);
        binding1.setVariable("result", args.get(0) + " " + args.get(1));
        return binding1;
    }
}
