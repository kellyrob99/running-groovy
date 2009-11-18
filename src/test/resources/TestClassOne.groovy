class TestClassOne
{
    def main(args)
    {
        def myArgs = args ?: null
        def result = args?.join(' ')
        println result
        println myArgs
    }
}
