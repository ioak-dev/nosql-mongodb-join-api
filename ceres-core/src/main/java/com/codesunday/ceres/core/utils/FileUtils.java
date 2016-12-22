/*
 * Copyright (C) 2016  Arun Kumar Selvaraj

 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>
 */

package com.codesunday.ceres.core.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.node.ArrayNode;
import org.codehaus.jackson.node.ObjectNode;

public class FileUtils {

	private static ObjectMapper mapper = new ObjectMapper();

	/**
	 * Read the content of a file pointed by the input absolute file path. And
	 * return the file content as text
	 * 
	 * @param file
	 * @return
	 * @throws IOException
	 */
	public static String readFile(String file) throws IOException {
		BufferedReader reader = new BufferedReader(new FileReader(file));
		String line = null;
		StringBuilder stringBuilder = new StringBuilder();

		try {
			while ((line = reader.readLine()) != null) {
				stringBuilder.append(line);
			}

			return stringBuilder.toString().trim();
		} finally {
			reader.close();
		}
	}

	/**
	 * List absolute path of all files under a given directory and its sub
	 * directories
	 * 
	 * @param directoryName
	 * @return
	 */
	public static List<File> listf(String directoryName) {

		// list file
		File directory = new File(directoryName);

		// get all the files from a directory
		File[] fList = directory.listFiles();

		List<File> fileList = new ArrayList();

		for (File file : fList) {
			// if (file.isFile()) {
			// System.out.println(file.getAbsolutePath());
			// }
			// else
			if (file.isDirectory()) {
				fileList.addAll(listf(file.getAbsolutePath()));
			} else {
				fileList.add(file);
			}
		}
		return fileList;
	}

	/**
	 * Read and convert an array of files into a list of objects
	 * 
	 * @param files
	 * @return
	 */
	public static List<ObjectNode> read(List<File> files) {

		List<ObjectNode> list = new ArrayList();

		for (File file : files) {

			list.addAll(read(file));

		}

		return list;

	}

	/**
	 * Read and convert a single file into a list of objects
	 * 
	 * @param file
	 * @return
	 */
	public static List<ObjectNode> read(File file) {

		List<ObjectNode> list = new ArrayList();

		try {
			String fileContent = FileUtils.readFile(file.getAbsolutePath());

			if (fileContent != null && !fileContent.isEmpty()) {
				if (fileContent.startsWith("{")) {

					ObjectNode json = (ObjectNode) mapper.readTree(fileContent);
					list.add(json);

				} else if (fileContent.startsWith("[")) {

					ArrayNode jsonArray = (ArrayNode) mapper.readTree(fileContent);

					for (JsonNode node : jsonArray) {
						list.add((ObjectNode) node);
					}
				}
			}

		} catch (IOException e) {
			e.printStackTrace();
		}

		return list;
	}

}
