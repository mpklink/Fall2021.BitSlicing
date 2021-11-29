from PIL import Image
import ImageLayer
import IPImage

class Processor:
    def __init__(self, filename):
        self.layers = ()
        self.currentLayer = -1
        self.canvasWidth = 0
        self.canvasHeight = 0
        if isinstance(filename, str):
            bufferedImage = Image.open(filename)
        else:
            bufferedImage = filename
        self.addLayer(ImageLayer(IPImage(bufferedImage)))
        self.inferCanvasSize(bufferedImage)

    def inferCanvasSize(self, bi):
        self.canvasWidth = bi.getWidth()
        self.canvasHeight = bi.getHeight()

    def currentLayer(self):
        return self.layers.get(self.currentLayer)

    def push(self):
        self.layers.add(self.currentLayer().clone())
        self.resetCurrentLayer()
        return self

    def resetCurrentLayer(self):
        self.currentLayer = self.layers.size() - 1

    def popLayer(self):
        self.layers.remove(self.currentLayer)
        self.currentLayer -= 1
        if (self.currentLayer < 0):
            self.currentLayer = 0
        return self

    def image(self):
        return self.currentLayer().image()

    def histogram(self):
        self.currentLayer().image().histogram()
        return self

    def histogram(self, i):
        self.currentLayer().image().histogram(1)
        return self

    def saveCurrentLayer(self, string):
        self.saveLayer(string)
        return self

    def brighten(self, i):
        self.currentLayer().image().brighten(i)
        return  self
        
    def addContrast(self, f):
        self.currentLayer().image().addContrast(f)
        return self

    def addLayer(self, layer):
        return self