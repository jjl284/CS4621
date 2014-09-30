package cs4620.common.texture;

import egl.math.Color;
import egl.math.MathHelper;
import egl.math.Vector2i;

public class TexGenUVGrid extends ACTextureGeneratorTwoColor {
	public TexGenUVGrid() {
		setSize(new Vector2i(128, 128));
		setColor1(Color.Red);
		setColor2(Color.Green);
	}
	
	@Override
	public void getColor(float u, float v, Color outColor) {
		outColor.set(
			MathHelper.clamp((int)Math.round(u * color1.R + v * color2.R), 0, 255),
			MathHelper.clamp((int)Math.round(u * color1.G + v * color2.G), 0, 255),	
			MathHelper.clamp((int)Math.round(u * color1.B + v * color2.B), 0, 255),	
			MathHelper.clamp((int)Math.round(u * color1.A + v * color2.A), 0, 255)
			);
	}
}
