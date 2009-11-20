import groovy.lang.Binding;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.Properties;

public class TestJavaOne
{
    Binding binding;

    public TestJavaOne()
    {
        binding = new Binding();
    }

    public TestJavaOne(Binding binding)
    {
        this.binding = binding;
    }

    public Object run()
    {
        List args = (List) binding.getVariable("args");
        binding.setVariable("myArgs", args);
        binding.setVariable("result", args.get(0) + " " + args.get(1));
        return binding;    
    }

    public static void main(String[] args)
    {
        System.out.println("Got called in Javaland! args = " + args);

        //alternative way to feed parameters
        Properties props = new Properties();
        URL url = ClassLoader.getSystemResource("testJavaOne.properties");
        try
        {
            props.load(url.openStream());
        }
        catch (IOException e)
        {
            e.printStackTrace();

        }
        assert (props.entrySet().size() == 2);
    }
}
