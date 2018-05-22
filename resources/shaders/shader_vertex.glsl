#version 120

// In
attribute vec2 position;
attribute vec3 color;
attribute vec2 texcoord;

// Out
varying vec3 vertexColor;
varying vec2 textureCoord;

uniform mat4 model;
uniform mat4 view;
uniform mat4 projection;

void main() {
	vertexColor = color;
	textureCoord = texcoord;
	//mat4 mvp = projection * view * model;
	gl_Position = vec4(position, 0.0, 1.0);
}

/*
in vec4 in_Position;
in vec4 in_Color;
in vec2 in_TextureCoord;

out vec4 pass_Color;
out vec2 pass_TextureCoord;

void main(void) {

	gl_Position = in_Position;

	pass_Color = in_Color;
	pass_TextureCoord = in_TextureCoord;
}
*/
