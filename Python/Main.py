import Processor
import IPImage
start = Processor("./in/horse.jpg").grayscale()

for i in range(8):
    j = i
    start.addLayer(IPImage().bitSlice(j))
