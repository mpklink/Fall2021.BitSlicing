import Processor
import IPImage

class Main:

    def main(self, args):
        self.main()

    def main(self):
        print("Normal Dithering")
        
    start = Processor("./in/horse.jpg").grayscale()

    for i in range(8):
        j = i
        start.addLayer(IPImage().bitSlice(j))
