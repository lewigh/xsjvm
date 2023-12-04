# xsjvm
## Demonstrational JVM (in development)
Under development.

Java 17 system requirements.

To run it, you need to unpack the runtime classes (which are stored in jmod format) beforehand, to do this go to the directory of your JVM and run the command:

jmod extract ./jmods/java.base.jmod --dir ./extractedrt

Then, before you start the virtual machine via terminal or IDE, simply specify the path to the "classes" directory, which is located in the "extractedrt" directory you created earlier. This should be set through program arguments.
Like that (on Win):
![image](https://github.com/lewigh/xsjvm/assets/21281158/b3c9bbf9-f30e-425c-9ea2-4047369087c8)

Currently, the JVM has limited support for executing code from the neighboring Sampler project. 


