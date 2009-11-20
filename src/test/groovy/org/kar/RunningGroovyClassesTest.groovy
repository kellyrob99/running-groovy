package org.kar

/**
 * Test the ability to load and run Groovy classes dynamically.
 */
class RunningGroovyClassesTest extends GroovyTestCase
{
    def static groovyClassOne = 'src/test/resources/TestClassOne.groovy'
    def helper = new RunningGroovyTestHelper()

    /**
     * Test dynamic execution of a Groovy class using GroovyShell. Calls the 'main' method and we redirect
     * System.out temporarily to capture the output.
     */
    void testGroovyShellWithClass()
    {
        def buf = new ByteArrayOutputStream()
        def newOut = new PrintStream(buf)
        def saveOut = System.out
        System.out = newOut
        def shell = new GroovyShell()
        shell.run(new File(groovyClassOne),  new ArrayList(helper.args) as String[] )
        System.out = saveOut
        def result = buf.toString().split()
        assert result[0] == helper.args[0]
        assert result[1] == helper.args[1]
    }

    /**
     * Test dynamic execution of a Groovy class passing parameters on the command line.
     */
    void testGroovyCallWithClass()
    {
        def proc = """groovy $groovyClassOne Hello World""".execute()
        proc.waitFor()
        def result = proc.text.split()
        assert result[0] == 'Hello'
        assert result[1] == 'World'
    }

    /**
     * BROKEN
     */
    void testGroovyScriptEngineWithClass()
    {
        Binding binding = helper.createBinding()
        def gse = new GroovyScriptEngine(new File('.').toURL())
        println "testGroovyScriptEngineWithClass = "+gse.run(groovyClassOne, binding)
        println binding.variables
        fail('fix this test please')
    }

    /**
     * Test dynamic execution of a Groovy class using GroovyClassLoader that exposes data in the shared Binding.
     */
    void testGroovyClassLoaderWithClass()
    {
        helper.useClassLoader(groovyClassOne)
    }
}
