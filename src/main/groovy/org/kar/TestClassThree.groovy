package org.kar
class TestClassThree extends Script
{
    java.lang.Object run()
    {
        binding.with
        {
            setVariable('myArgs', getVariable('args'))
            setVariable('result', getVariable('args')?.join(' '))
        }
    }
}
