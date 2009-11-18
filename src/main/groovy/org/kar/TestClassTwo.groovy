package org.kar
class TestClassTwo
{
    def Binding binding

    TestClassTwo(Binding binding)
    {
        this.binding = binding
    }

    def void run()
    {
        binding.with
        {
            setVariable('myArgs', getVariable('args'))
            setVariable('result', getVariable('args')?.join(' '))

        }
    }
}
