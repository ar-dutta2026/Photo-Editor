/**
 * Name: Aritra Dutta
 * Email: ardutta@ucsd.edu
 * PID: A17685487
 * Sources used: Lecture Notes
 * 
 * This file's purpose is to edit images. It will accomplish this by being able
 * to downscale images (reduce resolution), rotate images by a factor of 90
 * degrees, and be able to patch another image, into the desired image. 
 */


import javax.imageio.ImageIO; 
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
/**
 * The Class ImageEditor uses serveral methods and helper methods, to be
 * able to edit images. It loads up a given image in file, and assigns it to 
 * static 2d array image. Then through given methods it can rotate the image, 
 * downsize the image, and be able to patch another image into the original 
 * image. Also the image will be saved into the given file, with use of the 
 * save method. Also it can unpack bytes from decimal form, for use of the
 * downscaling method. Additionally it can pack together unpacked bytes, to 
 * create a pixel for the array (this is a helper method).
 */
public class ImageEditor {
    /* Constants (Magic numbers) */
    private static final String PNG_FORMAT = "png";
    private static final String NON_RGB_WARNING =
            "Warning: we do not support the image you provided. \n" +
            "Please change another image and try again.";
    private static final String RGB_TEMPLATE = "(%3d, %3d, %3d) ";
    private static final int BLUE_BYTE_SHIFT = 0;
    private static final int GREEN_BYTE_SHIFT = 8;
    private static final int RED_BYTE_SHIFT = 16;
    private static final int ALPHA_BYTE_SHIFT = 24;
    private static final int BLUE_BYTE_MASK = 0xff << BLUE_BYTE_SHIFT;
    private static final int GREEN_BYTE_MASK = 0xff << GREEN_BYTE_SHIFT;
    private static final int RED_BYTE_MASK = 0xff << RED_BYTE_SHIFT;
    private static final int ALPHA_BYTE_MASK = ~(0xff << ALPHA_BYTE_SHIFT);
    private static final int NINETY_DEGREE_CLOCKWISE = 90;

    /* Static variables - DO NOT add any additional static variables */
    static int[][] image;

    /**
     * Open an image from disk and return a 2D array of its pixels.
     * Use 'load' if you need to load the image into 'image' 2D array instead
     * of returning the array.
     *
     * @param pathname path and name to the file, e.g. "input.png",
     *                 "D:\\Folder\\ucsd.png" (for Windows), or
     *                 "/User/username/Desktop/my_photo.png" (for Linux/macOS).
     *                 Do NOT use "~/Desktop/xxx.png" (not supported in Java).
     * @return 2D array storing the rgb value of each pixel in the image
     * @throws IOException when file cannot be found or read
     */
    public static int[][] open(String pathname) throws IOException {
        BufferedImage data = ImageIO.read(new File(pathname));
        if (data.getType() != BufferedImage.TYPE_3BYTE_BGR &&
                data.getType() != BufferedImage.TYPE_4BYTE_ABGR) {
            System.err.println(NON_RGB_WARNING);
        }
        int[][] array = new int[data.getHeight()][data.getWidth()];

        for (int row = 0; row < data.getHeight(); row++) {
            for (int column = 0; column < data.getWidth(); column++) {
                /* Images are stored by column major
                   i.e. (2, 10) is the pixel on the column 2 and row 10
                   However, in class, arrays are in row major
                   i.e. [2][10] is the 11th element on the 2nd row
                   So we reverse the order of i and j when we load the image.
                 */
                array[row][column] = data.getRGB(column, row) & ALPHA_BYTE_MASK;
            }
        }

        return array;
    }

    /**
     * Load an image from disk to the 'image' 2D array.
     *
     * @param pathname path and name to the file, see open for examples.
     * @throws IOException when file cannot be found or read
     */
    public static void load(String pathname) throws IOException {
        image = open(pathname);
    }

    /**
     * Save the 2D image array to a PNG file on the disk.
     *
     * @param pathname path and name for the file. Should be different from
     *                 the input file. See load for examples.
     * @throws IOException when file cannot be found or written
     */
    public static void save(String pathname) throws IOException {
        BufferedImage data = new BufferedImage(
                image[0].length, image.length, BufferedImage.TYPE_INT_RGB);
        for (int row = 0; row < data.getHeight(); row++) {
            for (int column = 0; column < data.getWidth(); column++) {
                // reverse it back when we write the image
                data.setRGB(column, row, image[row][column]);
            }
        }
        ImageIO.write(data, PNG_FORMAT, new File(pathname));
    }

    /**
     * Unpack red byte from a packed RGB int
     *
     * @param rgb RGB packed int
     * @return red value in that packed pixel, 0 <= red <= 255
     */
    private static int unpackRedByte(int rgb) {
        return (rgb & RED_BYTE_MASK) >> RED_BYTE_SHIFT;
    }

    /**
     * Unpack green byte from a packed RGB int
     *
     * @param rgb RGB packed int
     * @return green value in that packed pixel, 0 <= green <= 255
     */
    private static int unpackGreenByte(int rgb) {
        return (rgb & GREEN_BYTE_MASK) >> GREEN_BYTE_SHIFT;
    }


    /**
     * Unpack blue byte from a packed RGB int
     *
     * @param rgb RGB packed int
     * @return blue value in that packed pixel, 0 <= blue <= 255
     */
    private static int unpackBlueByte(int rgb) {
        return (rgb & BLUE_BYTE_MASK) >> BLUE_BYTE_SHIFT;
    }

    /**
     * Pack RGB bytes back to an int in the format of
     * [byte0: unused][byte1: red][byte2: green][byte3: blue]
     *
     * @param red   red byte, must satisfy 0 <= red <= 255
     * @param green green byte, must satisfy 0 <= green <= 255
     * @param blue  blue byte, must satisfy 0 <= blue <= 255
     * @return packed int to represent a pixel
     */
    private static int packInt(int red, int green, int blue) {
        return (red << RED_BYTE_SHIFT)
                + (green << GREEN_BYTE_SHIFT)
                + (blue << BLUE_BYTE_SHIFT);
    }

    /**
     * Print the current image 2D array in (red, green, blue) format.
     * Each line represents a row in the image.
     */
    public static void printImage() {
        for (int[] ints : image) {
            for (int pixel : ints) {
                System.out.printf(
                        RGB_TEMPLATE,
                        unpackRedByte(pixel),
                        unpackGreenByte(pixel),
                        unpackBlueByte(pixel));
            }
            System.out.println();
        }
    }
    
    /**
     * This method rotates an image clockwise, given the amount of degrees the 
     * user wants the image to be rotated. Degrees must be greater than 0 and 
     * divisible by 90, else nothing will be done to image
     * @param degree used to see how much the image should be rotated (has to 
     * be divisible by 90 and not 0)
     */
    public static void rotate(int degree){
        //Sets the number of rotations to 0
        int rotation = 0;
        //If degree is 0 or is not a multiple of 90, nothing is done to image
        if(degree == 0 || degree % NINETY_DEGREE_CLOCKWISE != 0){
            return;
        }
        //The number of rotations is found by dividing 90 to degree
        rotation = degree/NINETY_DEGREE_CLOCKWISE;
        //The loop runs through amt of times the picture needs to be rotated
        for(int k = 1; k <= rotation; k++){
            //set to length of original array
            int numberOfRowInOld = image.length;
            //set to width of original array
            int numberOfColumnInOld = image[0].length;
            /* 
             * A new image is created with sizes opposite to that of the 
             * original image. 
             */ 
            int[][] newImage = new int[image[0].length][image.length];
            /*
             * The original image will now be gone through pixel by pixel, and
             * and each pixel (rgb value) will be assigned to the desired 
             * position of the rotated image. 
             */
            for(int i = 0; i < numberOfRowInOld; i++){
                for(int j = 0; j < numberOfColumnInOld; j++ ){
                    /* 
                     * The RGB value of old image, is now assined individually
                     * to each of the rotated image array
                     */
                    newImage[j][numberOfRowInOld - 1 - i] = image[i][j];                
                }
            }
            //new image is now set as old image
            image = newImage;
        }
        return;
    }
    
    /**
     * The method takes in the scale factor, and will reduce the image based on
     * the given scale factor. It will avg the rgb values of the pixels it 
     * needs to scale down to, and will give desired pixel after that. Program 
     * will not run if heightscale and widthscale is invalid. 
     * @param heightScale the height the image will be scaled down by
     * @param widthScale the width the image will be scaled down by
     */
    public static void downSample(int heightScale, int widthScale){
    /*
     * If heightscale or widthscale is invalid, nothing is done to image
     * Also degrees must be a multiple of 90.
     */
    if(heightScale < 1 ||widthScale < 1 || image.length % heightScale != 0
        || image[0].length % widthScale != 0 ){
        return;
    }
    //New image is created with the downscaled size. 
    int[][] newImage = new int[image.length/heightScale]
        [image[0].length/widthScale];
    //r, g, b, avgs are set to 0.
    int redAvg = 0;
    int greenAvg = 0;
    int blueAvg = 0;
    //starting point of row and column are set to 0
    int startingPointRow = 0;
    int startingPointColumn = 0;
    //The times will loop repeat based on length of image length / heightscale
    int repeat = image.length / heightScale;
    // the times the inner loop will repeat based on image length/ widthscale
    int repeatWidth = image[0].length / widthScale;
    /*
     * For every heightscale*widthscale pixels, it avgs out RGB components, 
     * done by utliizing the unpack helper methods, 
     * then it combines into one new pixel using the pack method to the new 
     * image at the desired place. 
     */
    for(int k = 0; k < repeat; k++ ){
        for(int l = 0; l < repeatWidth;l++){
            /*
             * it runs through original image 2d array and avgs out pixels, 
             * assigning into new values. 
             */
            for(int i = startingPointRow; i < heightScale+startingPointRow; 
                i++){
                for(int j = startingPointColumn; j < 
                    widthScale+startingPointColumn; j++){
                    //bytes are each unpacked based on respective colors. 
                    int red = unpackRedByte(image[i][j]);
                    int green = unpackGreenByte(image[i][j]);
                    int blue = unpackBlueByte(image[i][j]);
                    //bytes are then added together based on respective color
                    redAvg = redAvg+red;
                    greenAvg = greenAvg+green;
                    blueAvg = blueAvg+blue;
                }
            }
            /* 
             * bytes are avged on respective color, then they are packed
             * into a pixel, assigned to the newimage at the desired location
             */
            redAvg = redAvg/(heightScale*widthScale);
            greenAvg = greenAvg/(heightScale*widthScale);
            blueAvg = blueAvg/(heightScale*widthScale);
            newImage[k][l] = packInt(redAvg, greenAvg, blueAvg);
            //will start iteration at next widthscaled column
            startingPointColumn += widthScale;
            //Avgs are all set to 0
            redAvg = 0;
            greenAvg = 0;
            blueAvg = 0;
        }
        //will start iteration at next heightscale row
        startingPointRow += heightScale;
        //startingcolumn is set to 0 again
        startingPointColumn =0;        
    }
    //The image is now set to downscaled image
    image = newImage;
    return;
    }


    /**
     * The goal of this method is to patch another image into the image we 
     * start with. If startrow or start column is invalid, nothing will be 
     * done to image. 
     * @param startRow the row the image that will be patched will start in
     * @param startColumn the column the image that will be patched will start
     * @param patchImage the image that will be patched in array form
     * @param transparentRed the r value  of pixel that will not be patched
     * @param transparentGreen the g value of pixel that will not be patched
     * @param transparentBlue the b value of pixel that will not be patched
     * @return the amount of pixels that have been patched
     */
    public static int patch(int startRow, int startColumn, int[][] patchImage, 
        int transparentRed, int transparentGreen, int transparentBlue){
        /*
         * If startRow is negative, or greater than image length, it is invalid
         * If startColumn is negative,or greater than imagelength, it's invalid
         * If the end row of image is less than the end poing of patched 
         * image then it is also invalid
         * If end column of image is less than end column of patched image then
         * it is invalid
         * If it is invalid rest of method will not run, and 0 will be returned
         */
        if(startRow < 0 || startColumn < 0 || startRow > image.length || 
           startColumn > image[0].length ||
           image.length < startRow + patchImage.length ||
           image[0].length < startColumn + patchImage[0].length){
            return 0;
        }
        //The pixel value is created, using the packInt method and parameters
        int pixel = packInt(transparentRed, transparentGreen, transparentBlue);
        //counter is set to 0
        int counter = 0;
        //loop will run until patchImage has been completely patched to image
        for(int i = 0; i < patchImage.length; i++){
            for(int j = 0; j < patchImage[0].length; j++){
                //If the rgb is same at given i,j , iteration will be skipped
                if(patchImage[i][j] == pixel){
                    continue;
                }
                /* 
                 * Pixel at i,j for patched image will now be value of image
                 * at i+startrow, j+startcolumn. Also counter will add 1
                 * as pixel has been patched. 
                 */
                else{
                    image[i+startRow][j+startColumn] = patchImage[i][j];
                    counter++;
                }
                
            }
        }
        //returns the amount of pixels that have been patched
        return counter;
    }


    public static void main(String[] args) throws IOException {
        load("ucsd.png");
        downSample(4, 2);
        save("ucsdPLZWORKIMBEGGINGYOU.png");
    }
}