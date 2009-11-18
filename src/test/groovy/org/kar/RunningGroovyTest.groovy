package org.kar

class RunningGroovyTest extends GroovyTestCase
{
    def args = ['Hello', 'World'].asImmutable()

    void testGroovyShell()
    {
        def binding = new Binding()

        binding.setVariable('args', args)
        def shell = new GroovyShell(binding)
        shell.evaluate(new File('src/test/resources/TestScriptOne.groovy'))
        assertBinding(binding)
    }

    void testGroovyCall()
    {
        def proc = "groovy src/test/resources/TestScriptOne.groovy Hello World".execute()
        proc.waitFor()
        def args = proc.text.split()
        assert args[0] == 'Hello'
        assert args[1] == 'World'
    }

    void testGroovyScriptEngine()
    {
        def binding = new Binding()
        def args = ['Hello', 'World']
        binding.setVariable('args', args)
        def shell = new GroovyScriptEngine(new File('.').toURL())
        shell.run('src/test/resources/TestScriptOne.groovy', binding)
        assertBinding(binding)
    }

    void testGroovyClassLoader()
    {
        ClassLoader parent = getClass().getClassLoader();
        GroovyClassLoader loader = new GroovyClassLoader(parent);
        Class groovyClass = loader.parseClass(new File("src/test/resources/TestScriptOne.groovy"));

        def binding = new Binding()
        def args = ['Hello', 'World']
        binding.setVariable('args', args)
        GroovyObject groovyObject = (GroovyObject) groovyClass.newInstance();
        groovyObject.binding = binding
        groovyObject.invokeMethod("run", null);
        assertBinding(binding)
    }

    void testGroovyShellWithClass()
    {
        def binding = new Binding()
        binding.setVariable('args', args)
        def shell = new GroovyShell(binding)
        shell.evaluate(new File('src/test/resources/TestClassOne.groovy'))
        //assertBinding(binding)
    }

    void testGroovyCallWithClass()
    {
        def proc = "groovy src/test/resources/TestClassOne.groovy Hello World".execute()
        proc.waitFor()
        println "groovyCallWithClass: "+ proc.text
//        assert args[0] == 'Hello'
//        assert args[1] == 'World'
    }

    void testGroovyScriptEngineWithClass()
    {
        def binding = new Binding()
        def args = ['Hello', 'World']
        binding.setVariable('args', args)
        def shell = new GroovyScriptEngine(new File('.').toURL())
        println "running: "+shell.run('src/test/resources/TestClassOne.groovy', binding)
        //assertBinding(binding)
    }

    void testGroovyClassLoaderWithClass()
    {
        ClassLoader parent = getClass().getClassLoader();
        GroovyClassLoader loader = new GroovyClassLoader(parent);
        Class groovyClass = loader.parseClass(new File("src/test/resources/TestClassOne.groovy"));

        def args = ['Hello', 'World']
        GroovyObject groovyObject = (GroovyObject) groovyClass.newInstance();
        groovyObject.invokeMethod("main", args);
    }

    void testGroovyShellOnJava()
    {
        def binding = new Binding()
        def args = ['Hello', 'World']
        binding.setVariable('args', args)
        def shell = new GroovyShell(binding)
        shell.evaluate(new File('src/test/resources/TestJavaOne.java'))
        assert binding.getVariables().size() == 1
    }

    void testGroovyScriptEngineOnJava()
    {
        def binding = new Binding()
        def args = ['Hello', 'World'] as String[]
        binding.setVariable('args', args)
        def shell = new GroovyScriptEngine(new File('.').toURL())
        shell.run('src/test/resources/TestJavaOne.java', '')
        assert binding.getVariables().size() == 1
    }

    void testGroovyClassLoaderOnJava()
    {
        ClassLoader parent = getClass().getClassLoader();
        GroovyClassLoader loader = new GroovyClassLoader(parent);
        Class javaClass = loader.parseClass(new File("src/test/resources/TestJavaOne.java"));

        def args = ['Hello', 'World']
        def groovyObject = javaClass.newInstance();
        groovyObject.invokeMethod("main", args as String[]);
    }

    void testGroovyJavaCall()
    {
        def proc = "groovy src/test/resources/TestJavaOne.java Hello World".execute()
        proc.waitFor()
        assertTrue(proc.text.contains('cannot compile file with .java extension'))
    }

    private def assertBinding(Binding binding)
    {
        assert binding.variables.size() == 3
        assert binding.variables.args.value[0].toString() == args[0]
        assert binding.variables.args.value[1].toString() == 'World'
        assert binding.variables.result.value.toString() == 'Hello World'
        assert binding.variables.myArgs.value[0].toString() == 'Hello'
        assert binding.variables.myArgs.value[1].toString() == 'World'
    }
}
