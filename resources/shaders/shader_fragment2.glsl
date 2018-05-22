#version 130

uniform sampler2D texture1;

in vec4 pass_Color;
in vec2 pass_TextureCoord;

out vec4 out_Color;

void main(void) {
	out_Color = pass_Color;
	
	// Override out_Color with our texture pixel
	out_Color = pass_Color;
	out_Color = texture(texture1, pass_TextureCoord);
	
	gl_FragColor = vec4(0, 1, 1, 1);
}
