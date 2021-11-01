class ImageLayer:
    def __init__(self, image):
        self.image = image
        self.dx = 0
        self.dy = 0
        self.sx = 1
        self.sy = 1
        self.r = 0

    def image(self):
        return self.image

    def clone(self):
        return ImageLayer(self.image.clone())

    def getPixel(self, i, j):
        return self.image.getPixel(i, j)