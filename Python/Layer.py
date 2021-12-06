from PIL import Image, ImageDraw
import math

class Layer:
    def __init__(self, filename: str):
        self.image = Image.open(filename)

    def __init__(self, image: Image.Image):
        self.image = image

    def save(self, filename: str):
        ending = filename[len(filename) - 3]
        self.image.save(filename, ending)
        return self

    def rotatedNearestNeighbor(self, angle: float):
        toReturn = Image.new(mode="RGBA",size=(self.image.size[0], self.image.size[1]))
        px = toReturn.load()

        width = self.image.size[0]
        height = self.image.size[1]
        centerX = float(width) / 2.0
        centerY = float(height) / 2.0

        for y in range(height):
            for x in range(width):
                distanceX = float(x) - centerX
                distanceY = float(y) - centerY
                distance = math.sqrt(distanceX ** 2 + distanceY ** 2)
                postAngle = math.atan2(distanceY, distanceX)
                preAngle = postAngle - angle
                originalX = int((distance * math.cos(preAngle) + .5) + centerX)
                originalY = int((distance * math.sin(preAngle) + .5) + centerY)
                if (originalX < 0 or originalX >= width or originalY < 0 or originalY >= height):
                    continue

                pixel = self.image.getpixel((originalX, originalY))
                px[x,y] = pixel
        
        self.image = toReturn
        return self