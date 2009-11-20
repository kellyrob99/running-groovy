package org.kar

/**
 * Extraction of common creation and verification tasks used across the test suite.
 */
class RunningGroovyTestHelper
{
    def static args = ['Hello', 'World'].asImmutable()
    
    /**
     * Parse, instantiate and run the Class parsed from fileName
     * @param fileName  the fileName to parse and run
     */
    private def useClassLoader(fileName)
    {
        ClassLoader parent = getClass().getClassLoader();
        GroovyClassLoader loader = new GroovyClassLoader(parent);

        Class groovyClass = loader.parseClass(new File(fileName));

        Binding binding = createBinding()
        GroovyObject groovyObject = (GroovyObject) groovyClass.newInstance();
        groovyObject.binding = binding
        groovyObject.invokeMethod("run", null);
        assertBinding(binding)
    }

    /**
     * Create a Binding with a single parameter to be passed to scripts and an 'out' Writer to redirect console output.
     */
    private Binding createBinding()
    {
        Binding binding = new Binding()
        def sWriter = new StringWriter()
        def pWriter = new PrintWriter(sWriter)
        binding.setVariable ('args', new ArrayList(args))
        binding.setVariable ('out', pWriter)
        return binding
    }

    /**
     * Assert that the expected 'common' actions are done with the Binding by each of the use cases.
     * The original 'args' should be as expected.
     * A copy of 'args' should have been placed in the Binding during execution.
     * The 'result' should be the concatentation of 'args' separated by spaces.
     */
    private def assertBinding(Binding binding)
    {
        assert binding.variables.size() == 4
        assert binding.variables.args.value[0].toString() == args[0]
        assert binding.variables.args.value[1].toString() == args[1]
        assert binding.variables.result.value.toString() == args.join(' ')
        assert binding.variables.myArgs.value[0].toString() == args[0]
        assert binding.variables.myArgs.value[1].toString() == args[1]
    }
}
