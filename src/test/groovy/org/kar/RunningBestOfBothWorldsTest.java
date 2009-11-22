package org.kar;

import groovy.lang.Binding;
import groovy.lang.GroovyShell;
import groovy.util.GroovyScriptEngine;
import groovy.util.ResourceException;
import groovy.util.ScriptException;
import junit.framework.TestCase;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Demonstrating a Java class dynamically running Groovy code.
 */
public class RunningBestOfBothWorldsTest extends TestCase
{
    private RunningGroovyTestHelper helper = new RunningGroovyTestHelper();

    private String scriptName = "src/main/groovy/org/kar/BestOfBothWorlds.groovy";

    /**
     * Test running a Groovy script dynamically with GroovyScriptEngine.
     * @throws MalformedURLException
     * @throws ScriptException
     * @throws ResourceException
     */
    public void testBestOfBothWorldsWithScriptEngine() throws MalformedURLException, ScriptException, ResourceException
    {
        Binding binding = helper.createBinding();
        GroovyScriptEngine engine = new GroovyScriptEngine(new URL[]{new File(".").toURL()});
        Object o = engine.run(scriptName, binding);
        assertEquals("Hello World", o);
        helper.assertBinding(binding);
    }

    /**
     * Test evaluating a Groovy script dynamically with GroovyShell.
     * @throws IOException
     */
    public void testBestOfBothWorldsWithGroovyShell() throws IOException
    {
        Binding binding = helper.createBinding();
        GroovyShell engine = new GroovyShell(binding);
        Object o = engine.evaluate(new File(scriptName));
        assertEquals("Hello World", o);
        helper.assertBinding(binding);
    }

    /**
     * Test instantiating an internal class of a Script.
     */
    public void testInternalClass()
    {
        Binding binding = helper.createBinding();
        TestableClass aClass = new TestableClass();
        aClass.setBinding(binding);
        aClass.run();
        helper.assertBinding(binding);
    }

    /**
     * Test instantiating an internal class of a Script that in turn instantiates another internal class to delegate work to.
     */
    public void testInternalClass2()
    {
        Binding binding = helper.createBinding();
        TestableClass2 class2 = new TestableClass2(binding);
        class2.run();
        helper.assertBinding(binding);
    }
}
