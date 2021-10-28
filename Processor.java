import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;
import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.List;
import java.awt.Color;

import javax.imageio.ImageIO;
import java.awt.Graphics2D;

public class Processor {

  List<ImageLayer> layers = new ArrayList<>();
  int currentLayer = -1;
  int canvasWidth;
  int canvasHeight;

  public Processor(String filename) {
    if (filename.toLowerCase().endsWith(".ppm")) {
      try {
        List<String> lines = Files.readAllLines(Paths.get(filename));
        
      } catch (IOException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }

    } else {
      readStandardFormat(filename);
    }
  }

  private void readStandardFormat(String filename) {
    try {
      BufferedImage bufferedImage = ImageIO.read(new File(filename));
      // layers.add(new ImageLayer(new Image(bufferedImage)));
      addLayer(new ImageLayer(new IPImage(bufferedImage)));
      inferCanvasSize(bufferedImage);

    } catch (IOException ex) {
      ex.printStackTrace();
    }
  }

  public Processor(BufferedImage bi) {
    addLayer(new ImageLayer(new IPImage(bi)));
    // layers.add(new ImageLayer(new Image(bi)));
    inferCanvasSize(bi);
  }

  private void inferCanvasSize(BufferedImage bi) {
    this.canvasWidth = bi.getWidth();
    this.canvasHeight = bi.getHeight();
  }

  public ImageLayer currentLayer() {
    return this.layers.get(currentLayer);
  }

  public Processor push() {
    layers.add(currentLayer().clone());
    resetCurrentLayer();
    return this;
  }

  private void resetCurrentLayer() {
    currentLayer = layers.size() - 1;
  }

  public Processor popLayer() {
    // this.imageLayer = stack.pop();
    layers.remove(currentLayer);
    currentLayer--;
    if (currentLayer < 0)
      currentLayer = 0;
    return this;
  }

  public IPImage image() {
    return this.currentLayer().image();
  }

  public Processor histogram() {
    currentLayer().image().histogram();
    return this;
  }

  public Processor histogram(int i) {
    currentLayer().image().histogram(i);
    return this;
  }

  public Processor saveCurrentLayer(String string) {
    this.saveLayer(string);
    return this;
  }

  public Processor brighten(int i) {
    currentLayer().image().brighten(i);
    return this;
  }

  public Processor addContrast(float f) {
    currentLayer().image().addContrast(f);
    return this;
  }

  public Processor addLayer(IImageFunction i) {
    this.layers.add(new ImageLayer(i.run(currentLayer().image().clone())));
    resetCurrentLayer();

    return this;

  }

  public Processor addLayer(IPImage i) {
    this.layers.add(new ImageLayer(i));
    resetCurrentLayer();

    return this;

  }

  public Processor addLayer(ImageLayer layer) {
    layers.add(layer);
    resetCurrentLayer();
    return this;
  }

  public Processor addLayer(BufferedImage bufferedImage) {
    layers.add(new ImageLayer(new IPImage(bufferedImage)));
    resetCurrentLayer();
    return this;
  }

  public Processor saveLayer(String string) {
    if (string.toLowerCase().endsWith(".ppm")) {
      try {
        StringBuilder ppm = new StringBuilder();
        var image = currentLayer().image().image;

        // Magic Number
        ppm.append("P3\n");

        // Dimensions
        ppm.append(image.getWidth() + " " + image.getHeight() + "\n");

        // Max byte size
        ppm.append("255\n");

        // Loop over the pixels and add them to the file
        for (var h = 0; h < image.getHeight(); h++) {
          for (var w = 0; w < image.getWidth(); w++) {
            var pixelInt = image.getRGB(w, h);
            var color = new Color(pixelInt);
            ppm.append(color.getRed() + " " + color.getGreen() + " " + color.getBlue() + " ");
          }
        }
        Files.write(Paths.get(string), ppm.toString().getBytes());
      } catch (IOException e) {
        e.printStackTrace();
      }
      return this;
    }
    currentLayer().image().save(string);
    return this;
  }

  public Processor mergeLayers() {
    // Replace layers with a new rasterized copy of everything
    BufferedImage merged = new BufferedImage(canvasWidth, canvasHeight, BufferedImage.TYPE_4BYTE_ABGR);

    Graphics2D g = (Graphics2D) merged.getGraphics();

    for (ImageLayer layer : layers) {
      g.drawImage(layer.image().image, 0, 0, null);
    }

    g.dispose();

    this.clearLayers();
    this.addLayer(merged);
    return this;
  }

  private void clearLayers() {
    layers.clear();
    this.currentLayer = -1;
  }

  public Processor grayscale() {
    currentLayer().image().grayscale();

    return this;
  }

  public Color getPixel(int i, int j) {
    return currentLayer().getPixel(i, j);
    // return new Color(currentLayer().image().image.getRGB(i,j));
  }

  public Processor saveLayers(int[] is, String string) {
    BufferedImage merged = new BufferedImage(canvasWidth, canvasHeight, BufferedImage.TYPE_4BYTE_ABGR);

    Graphics2D g = (Graphics2D) merged.getGraphics();

    for (int layerNum : is) {
      g.drawImage(getLayer(layerNum).image().image, 0, 0, null);
    }

    g.dispose();

    new IPImage(merged).save(string);

    return this;
  }

  private ImageLayer getLayer(int layerNum) {
    return this.layers.get(layerNum);
  }

  public Processor applyCurve(IPixelFunction fun) {
    this.currentLayer().image().applyCurve(fun);
    return this;
  }

  public static IPImage ImageFromFunction(IPixelFunction fun) {

    int width = 256;
    int height = 256;
    var toReturn = new BufferedImage(256, height, BufferedImage.TYPE_4BYTE_ABGR);

    Graphics2D g = (Graphics2D) toReturn.getGraphics();
    g.setColor(Color.GRAY);
    g.fillRect(0, 0, width, height);
    for (int i = 0; i < 256; i++) {
      g.setColor(new Color(i, i, i));
      g.fillRect(i, 0, 1, height);
    }
    // Generate the histogram info

    int lastX = 0;
    int lastY = 0;
    for (int i = 0; i < 256; i++) {
      float x = i / 255f;
      float output = fun.run(x);
      output = Math.min(1, Math.max(0, output));
      float y = 1 - output;
      int j = (int) (y * 255);
      if (i != 0) {
        g.setColor(Color.BLACK);
        g.drawLine(lastX, lastY, i, j);
        g.setColor(Color.WHITE);
        g.drawLine(lastX - 1, lastY - 1, i - 1, j - 1);
      }
      // g.fillRect(i,j,1,1);
      lastX = i;
      lastY = j;

    }
    // for (var h = 0; h < height; h++) {
    // float y = 1 - h / (float) height;
    // if (y == .1)
    // System.out.println("here");
    // for (var w = 0; w < width; w++) {
    // float x = w / (float) width;
    // float output = fun.run(x);
    // output = Math.min(1, Math.max(0, output));
    // if (Math.abs(y - output) < 2 / (float) height) {
    // g.setColor(Color.WHITE);
    // g.fillRect(w, h, 1, 1);
    // }

    // }
    // }

    g.dispose();

    return new IPImage(toReturn);

  }

}
