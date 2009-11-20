package org.kar

/**
 * Test the ability to load and run Java files dynamically.
 */
class RunningJavaTest extends GroovyTestCase
{
    def static javaFileOne = 'src/test/resources/TestJavaOne.java'
    def static javaFileTwo = 'src/test/resources/TestJavaTwo.java'
    def helper = new RunningGroovyTestHelper()

    /**
     * Can call the java file, but cannot pass in any parameters to the main method.
     */
    void testGroovyShellOnJava()
    {
        Binding binding = helper.createBinding()
        def shell = new GroovyShell(binding)
        shell.evaluate(new File(javaFileOne))
        assert binding.getVariables().size() == 2
    }

    /**
     * Can call the java file, but cannot pass in any parameters to the main method.
     */
    void testGroovyScriptEngineOnJava()
    {
        Binding binding = helper.createBinding()
        def gse = new GroovyScriptEngine(new File('.').toURL())
        gse.run(javaFileOne, binding)
        assert binding.getVariables().size() == 2
    }

    /**
     * Dynamically compile, instantiate, inspect and call methods on a POJO.
     */
    void testGroovyClassLoaderOnJava()
    {
        ClassLoader parent = getClass().getClassLoader();
        GroovyClassLoader loader = new GroovyClassLoader(parent);
        Class javaClass = loader.parseClass(new File(javaFileOne));

        def groovyObject = javaClass.newInstance();
        def binding = helper.createBinding()
        groovyObject.binding = binding
        if(groovyObject.metaClass.respondsTo(groovyObject, 'run'))
        {
            groovyObject.invokeMethod("run", null);
            helper.assertBinding(binding)
        }
        if(groovyObject.metaClass.respondsTo(groovyObject, 'main'))
        {
            groovyObject.invokeMethod("main", new ArrayList(helper.args) as String[]);
        }
    }

    /**
     * Confirm that executing a Java file using the Groovy command line is not allowed.
     */
    void testGroovyJavaCall()
    {
        def proc = """groovy $javaFileOne Hello World""".execute()
        proc.waitFor()
        assertTrue(proc.text.contains('cannot compile file with .java extension'))
    }

    /**
    * If the Java file extends Script, all is good in the world.
    */
    void testGroovyJavaExtendsScript()
    {
        Binding binding = helper.createBinding()
        def shell = new GroovyShell(binding)
        shell.evaluate(new File(javaFileTwo))
        helper.assertBinding(binding)
    }
}
