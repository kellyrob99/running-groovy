package org.kar

/**
 * Baseline tests against compiled Groovy classes.
 */
class RunningCompiledGroovyTest extends GroovyTestCase
{
    def helper = new RunningGroovyTestHelper()

    /* Tests against compiled code. */

    /**
     * Test that a compiled script can expose data in the Binding.
     */
    void testCompiledGroovyScript()
    {
        Binding binding = helper.createBinding()
        new TestScriptTwo(binding).run()
        helper.assertBinding(binding)
    }

    /**
     * Test that a compiled groovy class can expose data in the Binding. Script behaviour is simulated by
     * implementing a 'run' method and supplying a constructor with a Binding param.
     */
    void testCompiledGroovyClass()
    {
        Binding binding = helper.createBinding()
        new TestClassTwo(binding).run()
        helper.assertBinding(binding)
    }

    /**
     * Test that a compiled groovy class extending Script is a nice marriage of both worlds.
     */
    void testCompiledGroovyClassExtendsScript()
    {
        Binding binding = helper.createBinding()
        new TestClassThree(binding:binding).run()
        helper.assertBinding(binding)
    }

}
