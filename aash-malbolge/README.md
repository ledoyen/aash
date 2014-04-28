# Aash Malbolge Interpreter

Basic Malbolge interpreter written in Scala.

> Here are some info to get started in Malbolge programming : http://esolangs.org/wiki/Malbolge_programming

To start running a Malbolge program, you can either run one of the embedded program :

```bash
> java -jar com.ledoyen.scala.aash.malbolge.Interpreter classpath helloWorld.mlb
```

Or use an external program :

```bash
> java -jar com.ledoyen.scala.aash.malbolge.Interpreter file "/home/some_text_file"
```

Or even pass an inline Malbolge program :

```bash
> java -jar com.ledoyen.scala.aash.malbolge.Interpreter inline (=<`:9876Z4321UT.-Q+*)M'&%$H\"!~}|Bzy?=|{z]KwZY44Eq0/{mlk**hKs_dG5[m_BA{?-Y;;Vb'rR5431M}/.zHGwEDCBA@98\6543W10/.R,+O<
```

** Beware ** when using the inline mode that double quotes `"` are naturally escaped by Java CLI, so escape them writing `\"` instead !
