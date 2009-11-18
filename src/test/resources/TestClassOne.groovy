class TestClassOne
{
    Binding binding

    static void main(String[] args)
    {
        def myArgs = args
        def result = args.join(' ')
        println result
        println myArgs
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
