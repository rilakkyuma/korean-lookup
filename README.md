# korean-lookup

A GUI that lets the user type in Korean and search for definitions and verb/adjective conjugations, without the need for a Korean keyboard. Originally made becauase I was tired of having to use an online keyboard to type Korean, copy that text into another tab, and search both the definition and conjugations in different windows. So I wrote a little program that did all of those in one. There are some limits though. First, the definitions are pulled from exact matches from [Kpedia](http://www.kpedia.jp/), so if the input is not found on there, then you might need to check the spelling, or perform an outside search (some words such as 왕가 simply do not have an entry). The verb/adjective conjugations are found on [Wiktionary](https://en.wiktionary.org/), so if verb/adjective conjugations are not found, then it's very likely you're not using the dictionary form, or the entry simply might not exist (much less likely).

## why are the definitions/etc in japanese?

The reason this app was written in Japanese is because I actually have a background of studying Japanese for six years, and Japanese/Korean translate very nicely to each other due to their similarities (similar to English/Spanish). Writing the GUI and having results be returned in Japanese was the most natural and efficient way to go about things.

## spelling

The search box will automatically replace the Roman characters typed once the `SPACE` bar is pressed, so hit `SPACE` for EACH Hangeul block you want to compose, as it will only convert the last valid block. The spelling is somewhat strict, as ᄀ ALWAYS is equivalent to 'g', not 'k', even at the end of a word. Similarly with ᄅ ('l'), ᄑ ('p'), ᄐ ('t'), etc...

## download

A pre-built runnable .jar file can be downloaded from [here](https://www.dropbox.com/s/tkf6vbpajsg12y3/Korean%20Lookup.jar?dl=0). Simply download the .jar and run it.
