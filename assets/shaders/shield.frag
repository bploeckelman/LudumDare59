#ifdef GL_ES
precision mediump float;
#endif

uniform sampler2D u_texture;
uniform sampler2D u_noise;
uniform float u_time;
uniform float u_health;

varying vec4 v_color;
varying vec2 v_texCoord;

vec4 getTexture(vec2 offset) {
    return texture2D(u_texture, v_texCoord + offset);
}

float edge(vec4 original) {
    float edge = 0.;
    if (original.r > 0.) {
        float pix = 1./1000.;
        edge += 1. - getTexture(vec2(0, pix)).r;
        edge += 1. - getTexture(vec2(pix, pix)).r;
        edge += 1. - getTexture(vec2(pix, 0)).r;
        edge += 1. - getTexture(vec2(pix, -pix)).r;
        edge += 1. - getTexture(vec2(0, -pix)).r;
        edge += 1. - getTexture(vec2(-pix, -pix)).r;
        edge += 1. - getTexture(vec2(-pix, 0)).r;
        edge += 1. - getTexture(vec2(-pix, pix)).r;
        edge = clamp(edge, 0., 1.);
    }
    return edge;
}

void main() {

    vec2 np = vec2(v_texCoord.x + (u_time/10.5), v_texCoord.y + (u_time/10.5));
    vec2 np2 = vec2(v_texCoord.x - (u_time/10.), v_texCoord.y + (u_time/10.));
    vec2 np3 = vec2(v_texCoord.x - (u_time/9.), v_texCoord.y - (u_time/9.));
    vec2 np4 = vec2(v_texCoord.x + (u_time/8.), v_texCoord.y - (u_time/8.));

    vec4 noise = texture2D(u_noise, np) + texture2D(u_noise, np2) +texture2D(u_noise, np3) + texture2D(u_noise, np4) ;
    noise /= 4.;

    float mainStength = (.8 + (sin(u_time*3.) * .3)) * u_health * noise.g;
    float hexStrength = (.8 + sin(u_time*2.) * .2) * u_health * noise.g;

    vec3 backColor = vec3(.137, .463, .882);
    vec3 lineColor = vec3(.137, .663, .882);

    vec4 texColor = getTexture(vec2(0.));

    vec3 shieldColor = mix(backColor, lineColor * hexStrength, texColor.g * .5);
    vec4 finalColor = vec4(texColor.r * shieldColor, texColor.b * mainStength);
//    gl_FragColor = vec4(edge(texColor));
    finalColor = mix(finalColor, vec4(backColor, 1.), edge(texColor));
    gl_FragColor = finalColor * v_color;
}
