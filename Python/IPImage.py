from PIL import Image, ImageDraw
import math
import copy

class IPImage:
    def __init__(self, image):
        if isinstance(image, Image.Image):
            self.image = image
        else:
            self.image = Image.open(image)
        self.px = self.image.load()

    def save(self, filename):
        self.image.save(filename, "PNG")
        return self

    def rotatedNearestNeighbor(self, angle):
        toReturn = Image.new(mode="RGBA",size=(self.image.size[0], self.image.size[1]))

        width = self.image.size[0]
        height = self.image.size[1]
        centerX = float(width)/2.0
        centerY = float(height)/2.0

        for y in range(height):
            for x in range(width):
                distanceX = x - centerX
                distanceY = y - centerY
                distance = math.sqrt(distanceX * distanceX + distanceY * distanceY)
                postAngle = math.atan2(distanceY, distanceX)
                preAngle = postAngle - angle
                originalX = int((distance * math.cos(preAngle) + .5) + centerX)
                originalY = int((distance * math.sin(preAngle) + .5) + centerY)
                if originalX < 0 or originalX >= width or originalY < 0 or originalY >= height:
                    continue
                else:
                    pixel = self.px[originalX,originalY]
                    toReturn.putpixel((x,y),pixel)

        self.image = toReturn
        return self

    def clone(self):
        return copy.deepcopy(self.image)