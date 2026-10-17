"""Generate animated intro layers from SCORIUM's original bundled artwork.

Keeps the normal header and poster untouched. Pillow is installed in the
Android Actions job only; the running Android app needs no Python dependency.
"""
from pathlib import Path
from PIL import Image, ImageDraw, ImageFilter, ImageChops
import sys

directory = Path(sys.argv[1])
logo = Image.open(directory / "scorium_header.png").convert("RGBA")
poster = Image.open(directory / "scorium_poster.png").convert("RGBA")
w, h = poster.size

# Remove the wordmark baked into the poster, leaving the original dark
# atmosphere and purple smoke in the corners for the intro background.
mask = Image.new("L", poster.size)
drawer = ImageDraw.Draw(mask)
drawer.rectangle((0, int(.28 * h), w, int(.65 * h)), fill=255)
mask = mask.filter(ImageFilter.GaussianBlur(radius=round(w * .035)))
base = Image.composite(Image.new("RGBA", poster.size, (0, 0, 2, 255)), poster, mask)
base.save(directory / "scorium_intro_background.png", optimize=True)

# A separately drifting translucent smoke layer cut from the same artwork.
fog = poster.copy()
smoke_mask = Image.new("L", poster.size)
drawer = ImageDraw.Draw(smoke_mask)
drawer.rectangle((0, 0, w, round(.33 * h)), fill=215)
drawer.rectangle((0, round(.72 * h), w, h), fill=215)
smoke_mask = smoke_mask.filter(ImageFilter.GaussianBlur(radius=round(.09 * w)))
maximum = ImageChops.lighter(
    ImageChops.lighter(fog.getchannel("R"), fog.getchannel("G")),
    fog.getchannel("B"),
)
maximum = maximum.point(lambda v: max(0, min(255, round((v - 10) * 1.7))))
fog.putalpha(ImageChops.multiply(maximum, smoke_mask))
fog.save(directory / "scorium_intro_fog.png", optimize=True)

# The I initially has NO popcorn on top. Extract its topping from the
# unchanged header and move an isolated real kernel from the U onto the I.
bare = logo.copy()
remove = Image.new("L", logo.size)
drawer = ImageDraw.Draw(remove)
drawer.rounded_rectangle((1138, 65, 1348, 258), radius=26, fill=255)
drawer.rectangle((1209, 235, 1315, 315), fill=255)
remove = remove.filter(ImageFilter.GaussianBlur(9))
bare.putalpha(ImageChops.subtract(bare.getchannel("A"), remove))
# Recreate the top of the I from its own white stem; retain original lettering.
stem = logo.crop((1209, 350, 1315, 351)).resize((106, 92), Image.Resampling.BILINEAR)
bare.alpha_composite(stem, (1209, 227))
bare.save(directory / "scorium_intro_wordmark.png", optimize=True)

kernel = logo.crop((1214, 74, 1330, 178))
kernel_mask = Image.new("L", kernel.size)
drawer = ImageDraw.Draw(kernel_mask)
drawer.ellipse((10, 4, kernel.width - 10, kernel.height - 3), fill=255)
kernel_mask = kernel_mask.filter(ImageFilter.GaussianBlur(5))
kernel.putalpha(ImageChops.multiply(kernel.getchannel("A"), kernel_mask))
kernel.save(directory / "scorium_intro_kernel.png", optimize=True)
print("Prepared SCORIUM intro artwork from original poster and wordmark.")
