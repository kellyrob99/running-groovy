package org.kar

class RunningGroovyScriptsTest extends GroovyTestCase
{
    def final static groovyScriptOne = "src/test/resources/TestScriptOne.groovy"
    def helper = new RunningGroovyTestHelper()

    /**
     * Test dynamic execution of a Groovy script using GroovyShell that exposes data in the shared Binding.
     */
    void testGroovyShell()
    {
        Binding binding = helper.createBinding()
        def shell = new GroovyShell(binding)
        shell.evaluate(new File(groovyScriptOne))
        helper.assertBinding(binding)
    }

    /**
     * Test dynamic execution of a Groovy script passing parameters on the command line.
     */
    void testGroovyCall()
    {
        def proc = """groovy $groovyScriptOne Hello World""".execute()
        proc.waitFor()
        def result = proc.text.split()
        assert result[0] == 'Hello'
        assert result[1] == 'World'
    }

    /**
     * Test dynamic execution of a Groovy script using GroovyScriptEngine that exposes data in the shared Binding.
     */
    void testGroovyScriptEngine()
    {
        Binding binding = helper.createBinding()
        def gse = new GroovyScriptEngine(new File('.').toURL())
        gse.run(groovyScriptOne, binding)
        helper.assertBinding(binding)
    }

    /**
     * Test dynamic execution of a Groovy script using GroovyClassLoader that exposes data in the shared Binding.
     */
    void testGroovyClassLoader()
    {
        helper.useClassLoader(groovyScriptOne)
    }
}
