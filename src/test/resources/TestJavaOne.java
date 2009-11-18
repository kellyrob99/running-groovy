import java.io.IOException;
import java.net.URL;
import java.util.Map;
import java.util.Properties;

public class TestJavaOne
{
    public static void main(String[] args)
    {
        System.out.println("Got called in Javaland! args = " + args);

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
