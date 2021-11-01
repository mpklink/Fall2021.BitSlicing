package Java;
public class Main {

    public static void main(String[] args) {
        var start = new Processor("./in/horse.jpg").grayscale();

        for (int i = 0; i < 8; i++) {
            final int j = i;
            start.addLayer(image -> image.bitSlice(j));
            start.saveLayers(new int[] { 1 }, "./out/sliced-" + i + ".png");
            start.popLayer();
        }

        for (int i = 0; i < 8; i++) {
            start = new Processor("./in/horse.jpg").grayscale();
            final int j = i;
            start.addLayer(image -> image.bitSlice(0, j));

            start.saveCurrentLayer("./out/sliced-combined-" + 0 + "-" + i + ".png");

        }

        for (int i = 0; i < 8; i++) {
            start = new Processor("./in/horse.jpg").grayscale();
            final int j = i;
            start.addLayer(image -> image.bitSlice(j, 7));

            start.saveCurrentLayer("./out/sliced-combined2-" + i + "-" + 7 + ".png");

        }

        // for(var i = 1; i < 10; i++){
        // start.push()
        // .brighten(i * 10)
        // .addLayer(image->image.histogram(100))
        // .saveLayers(new int[]{1,2} ,"./out/brighten" + i * 10 + ".png")
        // .popLayer()
        // .popLayer()
        // ;
        // }

        // IPixelFunction ipf = new IPixelFunction() {

        // @Override
        // public float run(float input) {
        // return input;
        // //return (float)Math.pow(input, .3);
        // //return 1-input;
        // //return input < .5 ? 0 : 1;

        // }

        // };

        // start.applyCurve(ipf)
        // .addLayer(Processor.ImageFromFunction(ipf))
        // .saveLayers(new int[]{0,1}, "./out/pixel-function.png");

    }
}