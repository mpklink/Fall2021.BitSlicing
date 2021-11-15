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
                    pixel = self.px[originalX, originalY]
                    toReturn.putpixel((x, y), pixel)

        self.image = toReturn
        return self

    def scaleNearestNeighbor(self, scaleX, scaleY):
        newWidth = int(self.image.size[0] * scaleX)
        newHeight = int(self.image.size[1] * scaleY)
        toReturn = Image.new(mode="RGBA",size=(self.image.size[0], self.image.size[1]))

        for y in range(newHeight):
            for x in range(newWidth):
                if x >= self.image.size[0] or y >= self.image.size[1]:
                    continue
                originalX = int(x / scaleX)
                originalY = int(y / scaleY)
                pixel = self.px[originalX, originalY]
                toReturn.putpixel((x, y), pixel)
        
        self.image = toReturn
        return self
    
    def translateNearestNeighbor(self, i, j):
        toReturn = Image.new(mode="RGBA",size=(self.image.size[0], self.image.size[1]))
        for y in range(toReturn.size[1]):
            for x in range(toReturn.size[0]):
                originalX = int(x - i + .5)
                originalY = int(y - j + .5)

                if originalX < 0 or originalX >= self.image.size[0] or y < 0 or y >= self.image.size[1]:
                    continue

                pixelInt = self.px[originalX, originalY]
                toReturn.putpixel((x, y), pixelInt)
        
        self.image = toReturn
        return self
        
    def clone(self):
        return copy.deepcopy(self.image)