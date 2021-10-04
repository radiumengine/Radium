#version 330 core

struct Light {

    vec3 position;
    vec3 color;
    float intensity;
    float attenuation;

};

struct Material {

    float reflectivity;

};

in vec3 vertex_position;
in vec2 vertex_textureCoord;
in vec3 vertex_normal;
in vec3 vertex_tangent;

in vec4 worldPosition;
in mat4 viewMatrix;

out vec4 outColor;

uniform sampler2D tex;

uniform Light lights[256];
uniform float ambient;

uniform Material material;

vec4 CalculateLight() {
    vec4 finalLight = vec4(0.0f);
    for (int i = 0; i < lights.length(); i++) {
        if (lights[i].intensity == 0) break;

        vec3 toLightVector = lights[i].position - worldPosition.xyz;
        vec3 toCameraVector = (inverse(viewMatrix) * vec4(0, 0, 0, 1)).xyz - worldPosition.xyz;
        vec3 unitNormal = normalize(vertex_normal);
        vec3 unitLightVector = normalize(toLightVector);
        vec3 unitCameraVector = normalize(toCameraVector);
        vec3 lightDirection = -unitLightVector;

        vec3 reflectedLightDirection = reflect(lightDirection, unitNormal);
        float specularFactor = dot(reflectedLightDirection, unitCameraVector);
        specularFactor = max(specularFactor, 0.0f);
        float dampedFactor = pow(specularFactor, 10.0f);
        vec3 specular = dampedFactor * material.reflectivity * lights[i].color;

        float nDotl = dot(unitNormal, unitLightVector);
        float brightness = max(nDotl, 0.0);
        vec3 diffuse = brightness * lights[i].color;

        float distanceFromLight = length(lights[i].position - vertex_position);
        float attenuation = 1.f / (1.f + lights[i].attenuation * distanceFromLight * 0.0075f * (distanceFromLight * distanceFromLight));

        diffuse *= attenuation;

        finalLight += (((vec4(diffuse, 1.f) * lights[i].intensity)) * attenuation * vec4(lights[i].color, 1.f) + vec4(specular, 0.0f));
    }

    return max(finalLight, ambient);
}

void main() {
	outColor = texture(tex, vertex_textureCoord) * CalculateLight();
}