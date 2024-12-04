import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.util.List;
import java.util.*;
import org.simplejavamail.api.email.Email;
import org.simplejavamail.api.mailer.Mailer;
import org.simplejavamail.api.mailer.config.TransportStrategy;
import org.simplejavamail.email.EmailBuilder;
import org.simplejavamail.mailer.MailerBuilder;

public class Main {

		static BufferedImage originalImage;
		
	
		public static void main(String[] args) throws IOException {
			System.out.println("\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n");
				automated(ImageIO.read(new URL(askForLink())));
		}

		public static String askForLink() {
				Scanner sc = new Scanner(System.in);
				System.out.print("Paste the link to the image: ");
				return sc.next();
		}

		public static void sendEmail(String filename, byte[] imageByteArray, byte[] htmlByteArray) {
			System.out.print("Email address to send file to: ");
			String recepient = new Scanner(System.in).nextLine();
			Email email = EmailBuilder.startingBlank()
							.to("Program User", recepient)
							.from("Desmos Art Bot", "imagetodesmosartbot@gmail.com")
							.withSubject("\"" + filename + "\" Desmos Art Program Render")
							.withPlainText("Thank you for trying my program! Attached below are the HTML file, which will open desmos with your image drawn when opened, and the PNG file, which is a render of what the desmos version will look like after opening it, without having to wait about 5-10 minutes for it to load (the wait time is caused by the desmos end, I can't do anything about it)")
							.withEmbeddedImage(filename + ".png", imageByteArray, "image/png")
							.withAttachment(filename + ".html", htmlByteArray, "application/html")
							.buildEmail();

			Mailer mailer = MailerBuilder.withSMTPServer("smtp.gmail.com", 465, "imagetodesmosartbot@gmail.com", "cnvb uvth zzyb rweu")
							.withTransportStrategy(TransportStrategy.SMTPS)
							.buildMailer();
			mailer.sendMail(email);
			System.out.println("email sent to " + recepient);
		}

		public static void automated(BufferedImage image) throws IOException {
				Scanner sc = new Scanner(System.in);
				System.out.print("Output HTML File Name: ");
				String outputFileName = sc.nextLine();
				image = fixTransparency(image);
				//ImageIO.write(image, "png", new File("downscaled(automated).png"));
			  doDownscalingAutomated(image);
				originalImage = ImageIO.read(new File("downscaled(automated).png"));
				BufferedImage i = simplifyColors(4, "downscaled(automated).png", 4, "Output(Automated).png", 150);
				generateEquations(ImageIO.read(new File("Output(Automated).png")), true, "Output(Automated).txt");
				writeToHTML("GeneratedEquations.txt", outputFileName + ".html");
				outlineWithColor(i, outputFileName + ".png", "Output(Automated).txt");
				System.out.println("Saved as " + outputFileName + ".html");
			  new File("downscaled(automated).png").delete();
				new File("Output(Automated).png").delete();
				new File("Output(Automated).txt").delete();
				new File("GeneratedEquations.txt").delete();
				byte[] imageByteArray = Files.readAllBytes(new File(outputFileName + ".png").toPath());
				byte[] htmlByteArray = Files.readAllBytes(new File(outputFileName + ".html").toPath());
				//new File(outputFileName + ".png").delete();
				//new File(outputFileName + ".html").delete();
				//sendEmail(outputFileName, imageByteArray, htmlByteArray);
		}

		public static BufferedImage fixTransparency(BufferedImage image) {
				for (int x = 0; x < image.getWidth(); x++) {
						for (int y = 0; y < image.getHeight(); y++) {
								int transparency = ((image.getRGB(x,y) & 0xff000000) >> 24);
								if (transparency == 0) {
										image.setRGB(x, y, 0xffffffff);
								}
						}
				}
				return image;
		}
		public static void doDownscalingAutomated(BufferedImage i) throws IOException {
				int x = i.getWidth();
				int y = i.getHeight();
				int timesToDownscale = 1;
				while ((x / timesToDownscale) * (y / timesToDownscale) > 75000) {
						timesToDownscale++;
				}
				BufferedImage downscaled = downscaleImage(i, timesToDownscale);
				ImageIO.write(downscaled, "png", new File("downscaled(automated).png"));
		}

		public static BufferedImage simplifyColors(int type, String filename, int numOfColors, String outputFileName, int diffInColors) throws IOException {
				Map<String, Integer> map = new HashMap<>();
				File file = new File(filename);
				BufferedImage image = ImageIO.read(file);
				int maxX = image.getWidth();
				int maxY = image.getHeight();
				for (int x = 0; x < maxX; x++) {
						for (int y = 0; y < maxY; y++) {
								int clr = image.getRGB(x, y);
								int red = (clr & 0x00ff0000) >> 16;
								int green = (clr & 0x0000ff00) >> 8;
								int blue = clr & 0x000000ff;
								String key = red + "," + green + "," + blue;
								map.putIfAbsent(key, 0);
								map.put(key, map.get(key) + 1);
						}
				}
				ArrayList<String> keys = new ArrayList<>();
				ArrayList<Integer> values = new ArrayList<>();
				for (int aaa = 0; aaa < numOfColors;aaa++) {
						keys.add("");
						values.add(0);
				}
				map.forEach((key, value) -> {
						String[] x = key.split(",");
						int r = Integer.parseInt(x[0]);
						int g = Integer.parseInt(x[1]);
						int b = Integer.parseInt(x[2]);
						//System.out.println(r + " " + g + " " + b);
						if (Math.max(Math.max(Math.abs(r - g), Math.abs(g - b)), Math.abs(r - b)) > diffInColors) {
								boolean f = false;
								for (int z = 0; z < keys.size(); z++) {
										String a = keys.get(z);
										String[] aa = a.split(",");
										if (aa.length == 3) {
												int r1 = Integer.parseInt(aa[0]);
												int g1 = Integer.parseInt(aa[1]);
												int b1 = Integer.parseInt(aa[2]);
												if (Math.abs(r1 - r) + Math.abs(g1 - g) + Math.abs(b1 - b) < 100) {
														if (type % 2 == 0) {
																if (value > values.get(z)) {
																		keys.set(z, key);
																		values.set(z, value);
																}
														}
														f = true;
														break;
												}
										}
								}
								if (!f) {
										for (int y = 0; y < values.size(); y++) {
												int v = values.get(y);
												if (value > v) {
														shift(keys, key, y);
														shift(values, value, y);
														break;
												}
										}
								}
						}
				});
				//System.out.println(values);
				//System.out.println(keys);
				if (type > 2 && type < 5 || type > 6) {
						keys.add("0,0,0");
						values.add(1000000000);
						keys.add("255,255,255");
						values.add(1000000001);
				}
				File outputPath = new File(outputFileName.split("\\.")[0] + ".txt");
				new FileWriter(outputPath, false).close();
				FileWriter fw = new FileWriter(outputPath, true);
				StringBuilder keyString = new StringBuilder();
				boolean removeFirst = true;
				for (String key : keys) {
						if (!removeFirst) keyString.append(" ");
						else removeFirst = false;
						keyString.append(key);
				}
				fw.write(keyString.toString());
				StringBuilder valueString = new StringBuilder();
				removeFirst = true;
				for (int value : values) {
						if (!removeFirst) valueString.append(" ");
						else removeFirst = false;
						valueString.append(value);
				}
				fw.write("\n");
				fw.write(valueString.toString());
				fw.close();
				for (int x = 0; x < maxX; x++) {
						for (int y = 0; y < maxY; y++) {
								int clr = image.getRGB(x, y);
								int red = (clr & 0x00ff0000) >> 16;
								int green = (clr & 0x0000ff00) >> 8;
								int blue = clr & 0x000000ff;
								String closestColor = "";
								int leastDif = 10000000;
								for (String key : keys) {
										if (!key.equals("")) {
												String[] s = key.split(",");
												int curRed = Integer.parseInt(s[0]);
												int curGreen = Integer.parseInt(s[1]);
												int curBlue = Integer.parseInt(s[2]);
												int dif = Math.abs(curRed - red) + Math.abs(curGreen - green) + Math.abs(curBlue - blue);
												if (dif < leastDif) {
														leastDif = dif;
														closestColor = key;
												}
										}
								}
								String[] c = closestColor.split(",");
								int r = Integer.parseInt(c[0]);
								int g = Integer.parseInt(c[1]);
								int b = Integer.parseInt(c[2]);
								int rgb = (r << 16 | g << 8 | b);
								image.setRGB(x, y, rgb);
						}
				}
				ImageIO.write(image, "png", new File(outputFileName));
				return image;
		}

		public static void shift(List l, Object valueToAdd, int locationToAddAt) {
				for (int z = l.size() - 1; z > locationToAddAt; z--) {
						l.set(z, l.get(z - 1));
				}
				l.set(locationToAddAt, valueToAdd);
		}


		public static void generateEquations(BufferedImage i, boolean useColor, String colorFileName) throws IOException {
				int h = i.getHeight();
				int w = i.getWidth();
				List<String> exportList = new ArrayList<>();
				String blackHex = "";
				String colorToAdd = "";
				List<String> keys = new ArrayList<>();
				List<Integer> values = new ArrayList<>();
				if (useColor) {
						blackHex = "', color: '#000000";
						Charset charset = Charset.defaultCharset();
						List<String> colorsList = Files.readAllLines(new File(colorFileName).toPath(), charset);
						keys.addAll(Arrays.asList(colorsList.get(0).split(" ")));
						for (String s : colorsList.get(1).split(" ")) {
								values.add(Integer.parseInt(s));
						}
				}

				exportList.add("x = 0 \\\\left\\\\{0 <= y <= " + h + "\\\\right\\\\}" + blackHex);
				exportList.add("x = " + w + " \\\\left\\\\{0 <= y <= " + h + "\\\\right\\\\}" + blackHex);

				for (int x = 0; x < w - 1; x++) {
						for (int y = 0; y < h; y++) {
								if (i.getRGB(x, y) != i.getRGB(x + 1, y)) {
										int yInv = h - y;
										if (useColor) {
												int clr = i.getRGB(x, y);
												int red = (clr & 0xff0000) >> 16;
												int green = (clr & 0x00ff00) >> 8;
												int blue = clr & 0x0000ff;
												int clr2 = i.getRGB(x + 1, y);
												int red2 = (clr2 & 0xff0000) >> 16;
												int green2 = (clr2 & 0x00ff00) >> 8;
												int blue2 = clr2 & 0x0000ff;

												int a = values.get(keys.indexOf(red + "," + green + "," + blue));
												int b = values.get(keys.indexOf(red2 + "," + green2 + "," + blue2));
												if (a < b) colorToAdd = "', color: '#" + Integer.toHexString(originalImage.getRGB(x, y)).substring(2);
												else colorToAdd = "', color: '#" + Integer.toHexString(originalImage.getRGB(x + 1, y)).substring(2);
										}
										exportList.add("y = " + yInv + " \\\\left\\\\{" + x + " < x < " + (x + 1) + "\\\\right\\\\}" + colorToAdd);
								}
						}
				}

				exportList.add("y = 0 \\\\left\\\\{0 <= x <= " + w + "\\\\right\\\\}" + blackHex);
				exportList.add("y = " + h + " \\\\left\\\\{0 <= x <= " + w + "\\\\right\\\\}" + blackHex);

				for (int x = 0; x < w; x++) {
						for (int y = 0; y < h - 1; y++) {
								if (i.getRGB(x, y) != i.getRGB(x, y + 1)) {
										int yInv = h - y;
										if (useColor) {
												int clr = i.getRGB(x, y);
												int red = (clr & 0xff0000) >> 16;
												int green = (clr & 0x00ff00) >> 8;
												int blue = clr & 0x0000ff;
												int clr2 = i.getRGB(x, y + 1);
												int red2 = (clr2 & 0xff0000) >> 16;
												int green2 = (clr2 & 0x00ff00) >> 8;
												int blue2 = clr2 & 0x0000ff;

												int a = values.get(keys.indexOf(red + "," + green + "," + blue));
												int b = values.get(keys.indexOf(red2 + "," + green2 + "," + blue2));
												if (a < b) colorToAdd = "', color: '#" + Integer.toHexString(originalImage.getRGB(x, y)).substring(2);
												else colorToAdd = "', color: '#" + Integer.toHexString(originalImage.getRGB(x, y + 1)).substring(2);
										}
										exportList.add("x = " + x + " \\\\left\\\\{" + (yInv - 1) + " < y < " + yInv + "\\\\right\\\\}" + colorToAdd);
								}
						}
				}
				FileWriter fw = new FileWriter("GeneratedEquations.txt");
				for (String str : exportList) fw.write(str + System.lineSeparator());
				fw.close();
		}

		public static void writeToHTML(String inputPath, String outputPath) throws IOException {
				File opening = new File("HTMLOpening.txt");
				File ending = new File("HTMLEnding.txt");
				new FileWriter(outputPath, false).close();
				FileWriter fw = new FileWriter(outputPath, true);
				Charset charset = Charset.defaultCharset();
				List<String> openingList = Files.readAllLines(opening.toPath(), charset);
				List<String> equationList = Files.readAllLines(new File(inputPath).toPath(), charset);
				List<String> endingList = Files.readAllLines(ending.toPath(), charset);
				for (String str : openingList) fw.write(str + "\n");
				for (int x = 1; x <= equationList.size(); x++) {
						String str = "       {id: '" + x + "', latex: '" + equationList.get(x - 1) + "'}";
						if (x != equationList.size()) str = str + ",\n";
						fw.write(str);
				}
				fw.write("\n");
				for (String str : endingList) fw.write(str + "\n");
				fw.close();
		}

		public static void outlineWithColor(BufferedImage i, String outputFile, String colorInputFileName) throws IOException {
				List<Integer> values = new ArrayList<>();
				Charset charset = Charset.defaultCharset();
				List<String> colorsList = Files.readAllLines(new File(colorInputFileName).toPath(), charset);
				List<String> keys = new ArrayList<>(Arrays.asList(colorsList.get(0).split(" ")));
				for (String s : colorsList.get(1).split(" ")) {
						values.add(Integer.parseInt(s));
				}

				int w = i.getWidth();
				int h = i.getHeight();
				BufferedImage output = new BufferedImage(i.getWidth(), i.getHeight(), 1);
				for (int x = 0; x < w; x++) {
						for (int y = 0; y < h; y++) {
								output.setRGB(x, y, Color.WHITE.getRGB());
						}
				}

				for (int x = 0; x < w - 1; x++) {
						for (int y = 0; y < h; y++) {
								if (i.getRGB(x, y) != i.getRGB(x + 1, y)) {
										int clr = i.getRGB(x, y);
										int red = (clr & 0xff0000) >> 16;
										int green = (clr & 0x00ff00) >> 8;
										int blue = clr & 0x0000ff;
										int clr2 = i.getRGB(x + 1, y);
										int red2 = (clr2 & 0xff0000) >> 16;
										int green2 = (clr2 & 0x00ff00) >> 8;
										int blue2 = clr2 & 0x0000ff;
										int a = values.get(keys.indexOf(red + "," + green + "," + blue));
										int b = values.get(keys.indexOf(red2 + "," + green2 + "," + blue2));
										if (a < b) output.setRGB(x, y, originalImage.getRGB(x, y));
										else output.setRGB(x, y, originalImage.getRGB(x + 1, y));
								}
						}
				}

				for (int x = 0; x < w; x++) {
						for (int y = 0; y < h - 1; y++) {
								if (i.getRGB(x, y) != i.getRGB(x, y + 1)) {
										int clr = i.getRGB(x, y);
										int red = (clr & 0xff0000) >> 16;
										int green = (clr & 0x00ff00) >> 8;
										int blue = clr & 0x0000ff;
										int clr2 = i.getRGB(x, y + 1);
										int red2 = (clr2 & 0xff0000) >> 16;
										int green2 = (clr2 & 0x00ff00) >> 8;
										int blue2 = clr2 & 0x0000ff;
										int a = values.get(keys.indexOf(red + "," + green + "," + blue));
										int b = values.get(keys.indexOf(red2 + "," + green2 + "," + blue2));
										if (a < b) output.setRGB(x, y, originalImage.getRGB(x, y));
										else output.setRGB(x, y, originalImage.getRGB(x, y + 1));

								}
						}
				}

				ImageIO.write(output, "png", new File(outputFile));
		}

		public static BufferedImage downscaleImage(BufferedImage imageToDownscale, int scale) {
				BufferedImage newImage = new BufferedImage((int) Math.floor(imageToDownscale.getWidth() / scale), (int) Math.floor(imageToDownscale.getHeight() / scale), 1);

				for (int x = 0; x < newImage.getWidth(); x++) {
						for (int y = 0; y < newImage.getHeight(); y++) {
								int r = 0;
								int b = 0;
								int g = 0;
								for (int xOff = 0; xOff < scale; xOff++) {
										for (int yOff = 0; yOff < scale; yOff++) {
												int clr = imageToDownscale.getRGB(x * scale + xOff, y * scale + yOff);
												r += (clr & 0x00ff0000) >> 16;
												g += (clr & 0x0000ff00) >> 8;
												b += clr & 0x000000ff;
										}
								}
								int scaleSqr = scale * scale;
								r = r/scaleSqr;
								g = g/scaleSqr;
								b = b/scaleSqr;
								newImage.setRGB(x, y, r << 16 | g << 8 | b);
						}
				}
				return newImage;
		}
}