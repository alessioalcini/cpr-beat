#!/usr/bin/env python3
"""Make 9:16 store screenshots from the 1080x2400 phone set without cropping anything.

RuStore crops every screenshot to 9:16 (cutting the status bar, the rate buttons and the
disclaimer off a 9:20 phone shot) and AppGallery recommends 450x800, also 9:16. This scales each
fastlane phone screenshot to 1920 px high and centers it on a 1080x1920 canvas in the app's
background colour (CprColor.Background), so the whole screen stays visible.

    python3 store/screenshots/pad_9x16.py

writes store/screenshots/9x16/{en-US,ru-RU}/*.png. Needs Pillow.
"""
from pathlib import Path

from PIL import Image

ROOT = Path(__file__).resolve().parents[2]
META = ROOT / "fastlane" / "metadata" / "android"
OUT = Path(__file__).resolve().parent / "9x16"
SIZE = (1080, 1920)
BACKGROUND = (0x0A, 0x0B, 0x0D)

for locale in ("en-US", "ru-RU"):
    target = OUT / locale
    target.mkdir(parents=True, exist_ok=True)
    for shot in sorted((META / locale / "images" / "phoneScreenshots").glob("*.png")):
        src = Image.open(shot).convert("RGB")
        scale = SIZE[1] / src.height
        resized = src.resize((round(src.width * scale), SIZE[1]), Image.Resampling.LANCZOS)
        canvas = Image.new("RGB", SIZE, BACKGROUND)
        canvas.paste(resized, ((SIZE[0] - resized.width) // 2, 0))
        canvas.save(target / shot.name, optimize=True)
        print(target.relative_to(ROOT) / shot.name)
