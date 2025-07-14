#ifdef GL_ES
precision mediump float;
#endif

varying vec2 v_texCoord0;
uniform sampler2D u_texture;

void main() {
    vec4 c = texture2D(u_texture, v_texCoord0);

    bool isGreen =
        (c.g > c.r + 0.05) &&
        (c.g > c.b + 0.05) &&
        (c.a > 0.0);

    if (isGreen) {
        float avg = (c.r + c.g + c.b) / 3.0;

        // Rich orange tone — golden and warm
        c.r = avg + 0.3;
        c.g = avg - 0.05;
        c.b = avg - 0.25;

        // Slight brightness & saturation boost
        c.rgb *= 1.05;
    }

    gl_FragColor = c;
}
