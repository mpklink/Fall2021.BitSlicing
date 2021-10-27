import java.awt.Color;

public class ImageLayer {
  IPImage image;
  float dx = 0;
  float dy = 0;
  float sx = 1;
  float sy = 1;
  float r = 0;

  public ImageLayer(IPImage image){
    this.image = image;
  }

  public IPImage image(){
    return this.image;
  }

  public ImageLayer clone(){
    ImageLayer toReturn = new ImageLayer(this.image.clone());
    return toReturn;
  }

  public Color getPixel(int i, int j) {
    return image.getPixel(i,j);
  }

  

}
