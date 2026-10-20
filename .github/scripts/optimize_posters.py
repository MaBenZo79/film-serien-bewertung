#!/usr/bin/env python3
"""Optimise the 273 bundled SCORIUM posters *only in the build workspace*.

The five source ZIP files committed to GitHub are deliberately never edited.
All names remain byte-for-byte unchanged so title SHA-256 poster mapping works.
"""
from pathlib import Path
from PIL import Image, ImageOps, UnidentifiedImageError
import argparse
import os

def optimise(root: Path):
    names_before = sorted(p.name for p in root.iterdir() if p.is_file())
    assert len(names_before) == 273, f"Expected 273 posters, got {len(names_before)}"
    assert all(len(n) == 28 and n.endswith(".jpg") and
               all(c in "0123456789abcdef" for c in n[:24]) for n in names_before), (
        "Poster file names no longer match app's title hashes"
    )
    total_before = 0
    total_after = 0
    changed = 0
    max_width = 1200
    max_height = 1500
    for name in names_before:
        source = root / name
        old_bytes = source.stat().st_size
        total_before += old_bytes
        temp = root / (name + ".optimized")
        try:
            with Image.open(source) as raw:
                raw.load()
                # Honour original JPEG EXIF rotation without changing the
                # title hash; the rendered image remains correctly oriented.
                im = ImageOps.exif_transpose(raw)
                if im.mode != "RGB":
                    im = im.convert("RGB")
                # More than sufficient for 112x154dp details and 4:3
                # collection tiles, including high-density Android screens.
                im.thumbnail((max_width, max_height), Image.Resampling.LANCZOS)
                im.save(temp, format="JPEG", quality=86,
                        subsampling=2, optimize=True, progressive=True)
            with Image.open(temp) as check:
                check.verify()
            if temp.stat().st_size < old_bytes:
                os.replace(temp, source)
                changed += 1
            else:
                temp.unlink()
        finally:
            if temp.exists():
                temp.unlink()
        total_after += source.stat().st_size
    names_after = sorted(p.name for p in root.iterdir() if p.is_file())
    assert names_after == names_before, "Poster count or file names changed during optimisation"
    print(f"Poster build asset optimisation: {len(names_after)} files preserved, "
          f"{changed} reduced; {total_before/1048576:.2f} MiB -> "
          f"{total_after/1048576:.2f} MiB "
          f"({100*(1-total_after/total_before):.1f}% saved).", flush=True)

if __name__ == "__main__":
    parser = argparse.ArgumentParser()
    parser.add_argument("poster_directory", type=Path)
    args = parser.parse_args()
    optimise(args.poster_directory)
