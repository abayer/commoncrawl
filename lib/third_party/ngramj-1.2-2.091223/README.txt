
This is 

        NGramJ

it is actually two independant sets of java classes:

1. the ngramj part, which is actually an rebuild of the text_cat PERL stuff 
(see http://odur.let.rug.nl/~vannoord/TextCat/) in Java. It tries to determine
the encoding and language to a sequence of bytes. In symbols:

    ngramj : byte[]  -->  (Language, Encoding)

2. the cngram this is the newer but right now more mature part. It's basic
function is to determine the language of a sequence of characters.

    cngram : char[]  --> Language

Note 1: This means given a file, ngramj can be immediately be applied, but
cngram needs additional information about the encoding. On the other hand if
you know the encoding, why let ngramj determine it. So both algorithms have
their applications.

Note 2: The basic principle of both ngram algorithms is statistical not to say
heuristical. Therefore you will not likely achieve 100% results. However given
enough text the methods get very, very close.


NGramJ is Open Source software released under the terms 
of the GNU Lesser General Public License. It is hosted
on Sourceforge. Use

    http://ngramj.sourceforge.net/

as an entry point.

Enjoy,
Frank

Installation:

1.) Phoner:
    java -classpath ngramj.jar de.spieleck.ngram.phoner.Phoner frank.lm a_phone_number

tries to convert a_phone_number into a easier to memorize string. Can be very
slow for long number, depends on the language resource given 
(try LM/English.lm instead of frank.lm)

2.) Langage classification:
    java -classpath ngramj.jar de.spieleck.ngram.lm.CathegorizerImpl LM a_text_file
:tries to figure the language/encoding of the text file a_text_file

3.) Generate new resource:
    java -classpath ngramj.jar de.spieleck.ngram.lm.LMWriter a_text_file a_resource_name.lm

converts the text file a_text_file into the resource a_resource_name, which 
can be used for classification task like the resources included.
