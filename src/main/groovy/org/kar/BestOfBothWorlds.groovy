package org.kar

/**
 * Classes inside of a Script.
 */
class TestableClass
{
    Binding binding

    def run()
    {
        binding.with
        {
            setVariable('myArgs', getVariable('args'))
            setVariable('result', getVariable('args')?.join(' '))
        }
        return binding
    }
}

class TestableClass2
{
    Binding binding

    public TestableClass2(Binding binding)
    {
        this.binding = binding;
    }

    def run()
    {
        return new TestableClass(binding: binding).run()
    }
}

if (args)
{
    def internalBinding = new Binding()
    internalBinding.setVariable('args', new ArrayList(args))
    internalBinding = new TestableClass2(internalBinding).run()
    args = internalBinding.args
    myArgs = internalBinding.myArgs
    result = internalBinding.result  //return value from script
}
else
{
    println 'no args!!'
}