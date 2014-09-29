Ordnance Survey OpenSpaces Scraper
==================================

Program to extract Ordnance Survey (OS) map tiles using an OS OpenSpaces API key and stitch together custom maps.

About
-----

This program allows you to specify a specific grid tile (in either numeric or alphanumeric format) for a place in Great Britain. You can then specify how many tiles you wish to download above/below and left/right of this tile. Each tile is downloaded sequentially using your API key and stored locally. Then you can ask the program to stitch together a full image containing the tiles you asked for.

The 'layers' box corresponds roughly to the layers box on the OS online map viewer. Experiment with it if you want, but if you just want 1:50000 maps then leave its default value alone.

This program was written by two hill walkers who wished to be able to stitch together custom maps. This is particularly useful for making maps of areas which span the edges of two OS maps, to print at home. This avoids the need to take two maps with you or switch between them while you're in the wild.

Notes
-----

The program is more or less complete. To use it, you need an OS OpenSpaces API key - try Googling it. This is a key which you register for your own domain or IP address. For the purposes of testing, you may wish to use the API key '6694613F8B469C97E0405F0AF160360A', which is the one used by the map viewer on the OS OpenSpaces website. Don't use this for your own archiving, just as a test.

Authors
-------

This was mainly written over a rainy weekend in summer 2011 by Sean Leavey and Ewan Gunn. Direct comments to Sean (and compaints to Ewan).

Sean Leavey
https://github.com/SeanDS/