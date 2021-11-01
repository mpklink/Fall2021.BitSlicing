from PIL import Image, ImageDraw
import copy

class IPImage:
    def __init__(self, filename):
        self.image = Image.Open(filename)

    def save(self, filename):
        self.image.save(filename, "PNG")
        return self

    def rotatedNearestNeighbor(self, angle):
        return self

    def clone(self):
        return copy.deepcopy(self.image)