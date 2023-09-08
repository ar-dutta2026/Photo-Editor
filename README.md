# Photo-Editor
This file's purpose is to edit images. It will accomplish this by being able to downscale images (reduce resolution), rotate images, and be able to patch another image, into the desired image. 

## Additional Description
The Class ImageEditor uses serveral methods and helper methods, to be able to edit images. It loads up a given image in file, and assigns it to static 2d array image. Then through given methods it can rotate the image, downsize the image, and be able to patch another image into the original image. Also the image will be saved into the given file, with use of the save method. Also it can unpack bytes from decimal form, for use of the downscaling method. Additionally it can pack together unpacked bytes, to create a pixel for the array (this is a helper method).
