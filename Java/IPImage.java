package Java;
import java.awt.image.BufferedImage;
import java.awt.Graphics2D;
import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.awt.Color;

public class IPImage {

  BufferedImage image;

  public IPImage(String filename) {
    try {
      BufferedImage bufferedImage = ImageIO.read(new File(filename));
      this.image = bufferedImage;

    } catch (IOException ex) {
      ex.printStackTrace();
    }
  }

  public IPImage(BufferedImage bi) {
    this.image = bi;
  }

  public IPImage save(String filename) {
    try {
      ImageIO.write(this.image, "PNG", new File(filename));
    } catch (IOException ex) {
      ex.printStackTrace();
    }
    return this;
  }

  public IPImage rotatedNearestNeighbor(double angle) {
    BufferedImage toReturn = new BufferedImage(this.image.getWidth(), this.image.getHeight(),
        BufferedImage.TYPE_INT_ARGB);

    int width = this.image.getWidth();
    int height = this.image.getHeight();
    double centerX = width / 2;
    double centerY = height / 2;

    for (int y = 0; y < height; y++) {
      for (int x = 0; x < width; x++) {

        // Get the distance and angle from the origin
        double distanceX = x - centerX;
        double distanceY = y - centerY;
        double distance = Math.sqrt(distanceX * distanceX + distanceY * distanceY);
        double postAngle = Math.atan2(distanceY, distanceX);
        double preAngle = postAngle - angle;
        int originalX = (int) ((distance * Math.cos(preAngle) + .5) + centerX);
        int originalY = (int) ((distance * Math.sin(preAngle) + .5) + centerY);
        if (originalX < 0 || originalX >= width || originalY < 0 || originalY >= height)
          continue;

        int pixel = this.image.getRGB(originalX, originalY);
        toReturn.setRGB(x, y, pixel);
      }
    }

    this.image = toReturn;
    return this;
  }

  public IPImage scaleNearestNeighbor(double scaleX, double scaleY) {
    int newWidth = (int) (this.image.getWidth() * scaleX);
    int newHeight = (int) (this.image.getHeight() * scaleY);
    BufferedImage toReturn = new BufferedImage(this.image.getWidth(), this.image.getHeight(),
        BufferedImage.TYPE_INT_ARGB);

    for (int y = 0; y < newHeight; y++) {
      for (int x = 0; x < newWidth; x++) {
        if (x >= this.image.getWidth() || y >= this.image.getHeight())
          continue;
        // Find the source pixel
        int originalX = (int) (x / scaleX);
        int originalY = (int) (y / scaleY);
        int pixel = this.image.getRGB(originalX, originalY);
        toReturn.setRGB(x, y, pixel);
      }
    }

    this.image = toReturn;
    return this;
  }

  public IPImage translateNearestNeighbor(double i, double j) {
    BufferedImage toReturn = new BufferedImage(this.image.getWidth(), this.image.getHeight(),
        BufferedImage.TYPE_INT_ARGB);
    for (var y = 0; y < toReturn.getHeight(); y++) {
      for (var x = 0; x < toReturn.getWidth(); x++) {
        int originalX = (int) (x - i + .5);
        int originalY = (int) (y - j + .5);

        if (originalX < 0 || originalX >= this.image.getWidth() || originalY < 0 || originalY >= this.image.getHeight())
          continue;

        var pixelInt = this.image.getRGB(originalX, originalY);
        toReturn.setRGB(x, y, pixelInt);
      }
    }

    this.image = toReturn;
    return this;
  }

  public IPImage translateLinear(double i, double j, boolean b) {
    BufferedImage toReturn = new BufferedImage(this.image.getWidth(), this.image.getHeight(),
        BufferedImage.TYPE_INT_ARGB);
    for (var y = 0; y < toReturn.getHeight(); y++) {
      for (var x = 0; x < toReturn.getWidth() - 1; x++) {
        int originalX = (int) (x - i + .5);
        int leftPixel = (originalX);
        int rightPixel = (originalX + 1);
        int originalY = (int) (y - j + .5);

        if (originalX < 0 || originalX >= this.image.getWidth() - 1 || originalY < 0
            || originalY >= this.image.getHeight() - 1)
          continue;

        Color pixelLeft = new Color(this.image.getRGB(leftPixel, originalY));
        Color pixelRight = new Color(this.image.getRGB(rightPixel, originalY));

        double percent = i - (int) i;
        Color leftPixelContibution = new Color((int) (pixelLeft.getRed() * (1 - percent)),
            (int) (pixelLeft.getGreen() * (1 - percent)), (int) (pixelLeft.getBlue() * (1 - percent)));
        Color rightPixelContibution = new Color((int) (pixelRight.getRed() * (percent)),
            (int) (pixelRight.getGreen() * (percent)), (int) (pixelRight.getBlue() * (percent)));

        int finalRed = leftPixelContibution.getRed() + rightPixelContibution.getRed();
        int finalGreen = leftPixelContibution.getGreen() + rightPixelContibution.getGreen();
        int finalBlue = leftPixelContibution.getBlue() + rightPixelContibution.getBlue();
        Color finalColor = new Color(finalRed, finalGreen, finalBlue);

        toReturn.setRGB(x, y, finalColor.getRGB());
      }
    }

    this.image = toReturn;
    return this;
  }

  public IPImage crop(int ulx, int uly, int lrx, int lry) {
    var width = lrx - ulx;
    var height = lry - uly;
    var toReturn = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

    for (var h = 0; h < height; h++) {
      for (var w = 0; w < width; w++) {
        var pixelInt = this.image.getRGB(w + ulx, h + uly);
        var pixelColor = new Color(pixelInt);
        toReturn.setRGB(w, h, pixelColor.getRGB());
      }
    }

    this.image = toReturn;
    return this;

  }

  public IPImage histogram() {
    return histogram(-1);
  }

  public IPImage histogram(int cap) {

    if (cap < 1)
      cap = 256;
    int height = cap;
    var toReturn = new BufferedImage(256, height, BufferedImage.TYPE_4BYTE_ABGR);

    // Generate the histogram info
    int[] counts = new int[256];
    for (var h = 0; h < this.image.getHeight(); h++) {
      for (var w = 0; w < this.image.getWidth(); w++) {
        Color pixel = new Color(this.image.getRGB(w, h));
        int red = pixel.getRed();
        int green = pixel.getGreen();
        int blue = pixel.getBlue();
        float[] hsv = new float[3];
        myConversion(red, green, blue, hsv);
        int value = (int) (hsv[2] * 255);
        if (value == 1)
          System.out.println();
        counts[value]++;

      }
    }

    // Render the histogram
    Graphics2D g = (Graphics2D) toReturn.getGraphics();
    g.setColor(Color.BLACK);
    g.fillRect(0, 0, counts.length, height);

    var maxValue = Arrays.stream(counts).max().orElse(0);
    // i//f (cap != -1)
    // maxValue = cap;
    for (var i = 0; i < counts.length; i++) {
      int percent = (int) (counts[i] / (double) maxValue * height);
      g.setColor(Color.WHITE);
      g.fillRect(i, height - percent, 1, percent);
    }

    this.image = toReturn;
    return this;

  }

  private static void myConversion(int r, int g, int b, float[] hsv) {
    float hue = -1;
    float saturation = -1;
    float value = -1;

    float red = (float) (r / 255.0);
    float green = (float) (g / 255.0);
    float blue = (float) (b / 255.0);

    float cMax = Math.max(Math.max(red, green), blue);
    value = cMax;
    float cMin = Math.min(Math.min(red, green), blue);
    float delta = cMax - cMin;

    if (cMax == 0) {
      hue = 0;
      value = 0;
      saturation = 0;
    } else {
      if (delta == 0) {
        hue = 0;
        saturation = (cMax - cMin) / cMax;

      } else {

        saturation = (cMax - cMin) / cMax;

        if (cMax == red)
          hue = (60 * (green - blue) / delta + 0) % 360;
        else if (cMax == green)
          hue = (60 * (blue - red) / delta + 120) % 360;
        else if (cMax == blue)
          hue = (60 * (red - green) / delta + 240) % 360;

        hue /= 360;
      }
    }

    // Stuff

    hsv[0] = hue;
    hsv[1] = saturation;
    hsv[2] = value;

  }

  public IPImage brighten(int i) {

    for (var h = 0; h < this.image.getHeight(); h++) {
      for (var w = 0; w < this.image.getWidth(); w++) {

        var pixelInt = this.image.getRGB(w, h);
        var pixelColor = new Color(pixelInt);

        float[] hsv = new float[3];

        myConversion(pixelColor.getRed(), pixelColor.getGreen(), pixelColor.getBlue(), hsv);

        var value = hsv[2];
        value += i / 255.0f;
        value = Math.min(1.0f, Math.max(0, value));
        var newColor = Color.getHSBColor(hsv[0], hsv[1], value);

        float[] again = new float[3];
        myConversion(newColor.getRed(), newColor.getGreen(), newColor.getBlue(), again);

        this.image.setRGB(w, h, newColor.getRGB());
      }
    }
    return this;
  }

  public IPImage addContrast(float amount) {
    for (var h = 0; h < this.image.getHeight(); h++) {
      for (var w = 0; w < this.image.getWidth(); w++) {

        var pixelInt = this.image.getRGB(w, h);
        var pixelColor = new Color(pixelInt);

        float[] hsv = new float[3];

        myConversion(pixelColor.getRed(), pixelColor.getGreen(), pixelColor.getBlue(), hsv);

        var value = hsv[2];
        var adjustedValue = value - .5f;
        adjustedValue *= amount;
        adjustedValue += .5;
        adjustedValue = Math.min(1.0f, Math.max(0, adjustedValue));
        var newColor = Color.getHSBColor(hsv[0], hsv[1], adjustedValue);

        float[] again = new float[3];
        myConversion(newColor.getRed(), newColor.getGreen(), newColor.getBlue(), again);

        this.image.setRGB(w, h, newColor.getRGB());
      }
    }

    return this;
  }

  public IPImage grayscale() {
    for (var h = 0; h < this.image.getHeight(); h++) {
      for (var w = 0; w < this.image.getWidth(); w++) {

        var pixelInt = this.image.getRGB(w, h);
        var pixelColor = new Color(pixelInt);

        float[] hsv = new float[3];
        myConversion(pixelColor.getRed(), pixelColor.getGreen(), pixelColor.getBlue(), hsv);

        int value = (int) Math.floor(Math.min(255, hsv[2] * 255.0f));

        this.image.setRGB(w, h, new Color(value, value, value).getRGB());
      }
    }
    return this;

  }

  public IPImage clone() {

    // See https://stackoverflow.com/a/19327237/10047920
    BufferedImage b = new BufferedImage(this.image.getWidth(), this.image.getHeight(), this.image.getType());
    Graphics2D g = (Graphics2D) b.getGraphics();
    g.drawImage(this.image, 0, 0, null);
    g.dispose();

    IPImage toReturn = new IPImage(b);
    return toReturn;
  }

  public Color getPixel(int i, int j) {
    return new Color(image.getRGB(i, j));
  }

  public IPImage applyCurve(IPixelFunction fun) {
    for (var h = 0; h < this.image.getHeight(); h++) {
      for (var w = 0; w < this.image.getWidth(); w++) {

        var pixelInt = this.image.getRGB(w, h);
        var pixelColor = new Color(pixelInt);

        float[] hsv = new float[3];
        myConversion(pixelColor.getRed(), pixelColor.getGreen(), pixelColor.getBlue(), hsv);

        float value = Math.max(0, Math.min(1, fun.run(hsv[2])));


        this.image.setRGB(w, h, new Color(value, value, value).getRGB());
      }
    }
    return this;
  }

  public IPImage bitSlice(int power){

    BufferedImage b = new BufferedImage(this.image.getWidth(), this.image.getHeight(), this.image.getType());
    for (var h = 0; h < b.getHeight(); h++) {
      for (var w = 0; w < b.getWidth(); w++) {
        //Assume we are in grayscale
        var pixelInt = this.image.getRGB(w, h);
        int value = new Color(pixelInt).getRed();
        int slicer = (int)Math.pow(2, power);
        int sliced = value & slicer;
        Color finalColor = null;
        if(sliced > 0){
          finalColor = Color.WHITE;
        }
        else{
          finalColor = Color.BLACK;
        }

        b.setRGB(w, h, finalColor.getRGB());


      }
    }




    IPImage toReturn = new IPImage(b);
    return toReturn;
    
  }
  public IPImage bitSlice(int powerLow, int powerHigh){
    if(powerLow == powerHigh) return this.bitSlice(powerLow);


    BufferedImage b = new BufferedImage(this.image.getWidth(), this.image.getHeight(), this.image.getType());
    for (var h = 0; h < b.getHeight(); h++) {
      for (var w = 0; w < b.getWidth(); w++) {
        //Assume we are in grayscale
        var pixelInt = this.image.getRGB(w, h);
        int value = new Color(pixelInt).getRed();
        int slicer = 0;
        for(int i = powerLow; i <= powerHigh; i++){
          slicer |= (int)Math.pow(2, i);

        }
        int sliced = value & slicer;
        sliced <<=7-powerHigh;
        Color finalColor = new Color(sliced, sliced, sliced);
        

        b.setRGB(w, h, finalColor.getRGB());


      }
    }




    IPImage toReturn = new IPImage(b);
    return toReturn;
    
  }

}