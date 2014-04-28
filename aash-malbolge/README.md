# Aash Malbolge Interpreter

Basic Malbolge interpreter written in Scala.

> Here are some info to get started in Malbolge programming : http://esolangs.org/wiki/Malbolge_programming

To use Aash Malbolge Interpreter, you can use the available CLI :

```bash
> java -jar aash-malbolge.jar <command> <type> <content>
```

__command :__ run or normalize

__type :__ classpath, file or inline

__content :__ according to the type : classpath location, path or in-line program

## Run a program
To start running a Malbolge program, you can either run one of the embedded program :

```bash
> java -jar aash-malbolge.jar run classpath helloWorld.mlb
```

Or use an external program :

```bash
> java -jar aash-malbolge.jar run file "/home/some_text_file"
```

Or even pass an inline Malbolge program :

```bash
> java -jar aash-malbolge.jar run inline (=<`:9876Z4321UT.-Q+*)M'&%$H\"!~}|Bzy?=|{z]KwZY44Eq0/{mlk**hKs_dG5[m_BA{?-Y;;Vb'rR5431M}/.zHGwEDCBA@98\6543W10/.R,+O<
```

__Beware__ when using the inline mode that double quotes `"` are naturally escaped by Java CLI, so escape them writing `\"` instead !


## Normalize a program
To normalize a Malbolge program, simply run the following :

```bash
> java -jar aash-malbolge.jar normalize classpath helloWorld.mlb
```

All previously used types (__classpath__, __file__ and __inline__) are available for `normalize` command.


## Samples

### Running embedded HelloWorld program
```bash
> java -jar aash-malbolge.jar run classpath helloWorld.mlb
```

```bash
Hello, world.
END [a=29486 c=115 d=94]
```

### Normalizing embedded Cat program
```bash
> java -jar aash-malbolge.jar normalize classpath cat.mlb
```

```bash
jiooooooooooooooooooooooo<ioooooooooooooooj/iooooo
oooooooooojiooooooooooooooooooooooooooooooo**o**j<
v*oopooooooooo/oooo/oo
j
ppopppp*ooooooooooooo*ooooj
o*oopoooooooooooooooo*ooooj
ooo*ooopooopooooooooo*ooooj
oooooooo*oooooooooooooooooj
ooopopooooooooooooooo*ooooj
o*oooopoooooooooooooo*ooooj
ooooooooooooooooooooo*ooooj
ooooooooooooooooooooo*ooooj
ooooooooooooooooooooo*ooooj
ooooooooooooooooooooo*ooooj
ooooooooooooooooooooo*ooooj
oooooopoooopooooooooooooooj
ooooooooooi
```
