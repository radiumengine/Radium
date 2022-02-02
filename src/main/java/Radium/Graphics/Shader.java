package Radium.Graphics;

import java.nio.FloatBuffer;

import Radium.Math.Vector.*;
import Radium.Util.FileUtility;
import RadiumEditor.Console;
import org.joml.Matrix4f;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL31;
import org.lwjgl.system.MemoryStack;

public class Shader {

	public transient String vertexFile, fragmentFile;
	private transient int vertexID, fragmentID, programID;
	
	public Shader(String vertexPath, String fragmentPath) {
		vertexFile = FileUtility.LoadAsString(vertexPath);
		fragmentFile = FileUtility.LoadAsString(fragmentPath);

		CreateShader();
	}
	
	private void CreateShader() {
		programID = GL20.glCreateProgram();
		vertexID = GL20.glCreateShader(GL20.GL_VERTEX_SHADER);
		
		GL20.glShaderSource(vertexID, vertexFile);
		GL20.glCompileShader(vertexID);
		
		if (GL20.glGetShaderi(vertexID, GL20.GL_COMPILE_STATUS) == GL11.GL_FALSE) {
			System.err.println("Vertex Shader: " + GL20.glGetShaderInfoLog(vertexID));
			return;
		}
		
		fragmentID = GL20.glCreateShader(GL20.GL_FRAGMENT_SHADER);
		
		GL20.glShaderSource(fragmentID, fragmentFile);
		GL20.glCompileShader(fragmentID);
		
		if (GL20.glGetShaderi(fragmentID, GL20.GL_COMPILE_STATUS) == GL11.GL_FALSE) {
			System.err.println("Fragment Shader: " + GL20.glGetShaderInfoLog(fragmentID));
			return;
		}
		
		GL20.glAttachShader(programID, vertexID);
		GL20.glAttachShader(programID, fragmentID);
		
		GL20.glLinkProgram(programID);
		if (GL20.glGetProgrami(programID, GL20.GL_LINK_STATUS) == GL11.GL_FALSE) {
			System.err.println("Program Linking: " + GL20.glGetProgramInfoLog(programID));
			return;
		}
		
		GL20.glValidateProgram(programID);
		if (GL20.glGetProgrami(programID, GL20.GL_VALIDATE_STATUS) == GL11.GL_FALSE) {
			System.err.println("Program Validation: " + GL20.glGetProgramInfoLog(programID));
			return;
		}
		
		GL20.glDeleteShader(vertexID);
		GL20.glDeleteShader(fragmentID);
	}
	
	public int GetUniformLocation(String name) {
		return GL20.glGetUniformLocation(programID, name);
	}
	
	public void SetUniform(String name, float value) {
		GL20.glUniform1f(GetUniformLocation(name), value);
	}
	
	public void SetUniform(String name, int value) {
		GL20.glUniform1i(GetUniformLocation(name), value);
	}
	
	public void SetUniform(String name, boolean value) {
		GL20.glUniform1i(GetUniformLocation(name), value ? 1 : 0);
	}
	
	public void SetUniform(String name, Vector2 value) {
		GL20.glUniform2f(GetUniformLocation(name), value.x, value.y);
	}
	
	public void SetUniform(String name, Vector3 value) {
		GL20.glUniform3f(GetUniformLocation(name), value.x, value.y, value.z);
	}

	public void SetUniform(String name, Matrix4f value) {
		if (value == null) return;

		try (MemoryStack stack = MemoryStack.stackPush()) {
			FloatBuffer fb = stack.mallocFloat(16);
			value.get(fb);
			GL20.glUniformMatrix4fv(GetUniformLocation(name), false, fb);
		}
	}

	public int GetInt(String name) {
		return GL31.glGetUniformi(programID, GetUniformLocation(name));
	}

	public float GetFloat(String name) {
		return GL31.glGetUniformf(programID, GetUniformLocation(name));
	}

	public void Bind() {
		GL20.glUseProgram(programID);
	}
	
	public void Unbind() {
		GL20.glUseProgram(0);
	}
	
	public void Destroy() {
		GL20.glDetachShader(programID, vertexID);
		GL20.glDetachShader(programID, fragmentID);
		GL20.glDeleteShader(vertexID);
		GL20.glDeleteShader(fragmentID);
		GL20.glDeleteProgram(programID);
	}

	public int GetProgram() {
		return programID;
	}
}