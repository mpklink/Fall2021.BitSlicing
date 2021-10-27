
public class Main {

    public static void main(String[] args) {
        var start = new Processor("./in/horse.jpg").grayscale();
        
        for(var i = 1; i < 10; i++){
            start.push()
            .brighten(i * 10)
            .addLayer(image->image.histogram(100))
            .saveLayers(new int[]{1,2} ,"./out/brighten" + i * 10 + ".png")
            .popLayer()
            .popLayer()
            ;
        }

        IPixelFunction ipf = new IPixelFunction() {

            @Override
            public float run(float input) {
               return input;
                //return (float)Math.pow(input, .3);
               //return 1-input;
               //return input < .5 ? 0 : 1;
                
            }
            
        };

        start.applyCurve(ipf)
            .addLayer(Processor.ImageFromFunction(ipf))
            .saveLayers(new int[]{0,1}, "./out/pixel-function.png");
        

        
        
    }
}
