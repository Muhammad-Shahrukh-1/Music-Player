import javax.sound.sampled.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.util.LinkedList;

public class Main {
    private static Clip clip;
    private static LinkedList<String> playlist = new LinkedList<>();
    private static int currentIndex = 0;
    private static Timer timer;
    private static JLabel songLabel;
    private static JLabel currentDurationLabel;
    private static JSlider songSlider;

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (UnsupportedLookAndFeelException | ClassNotFoundException | InstantiationException
                | IllegalAccessException e) {
            e.printStackTrace();
        }
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        fileChooser.setDialogTitle("Select the folder containing WAV files");

        int result = fileChooser.showOpenDialog(null);

        if (result == JFileChooser.APPROVE_OPTION) {
            File selectedFolder = fileChooser.getSelectedFile();
            File[] wavFiles = selectedFolder.listFiles((dir, name) -> name.toLowerCase().endsWith(".wav"));

            if (wavFiles != null && wavFiles.length > 0) {
                String[] playlistArray = new String[wavFiles.length];
                for (int i = 0; i < wavFiles.length; i++) {
                    playlistArray[i] = wavFiles[i].getAbsolutePath();
                }

                mergeSort(playlistArray, 0, playlistArray.length - 1);

                for (String audioFile : playlistArray) {
                    playlist.add(audioFile);
                }
            } else {
                System.out.println("No WAV files found in the selected folder.");
                return;
            }
        } else {
            System.out.println("User canceled folder selection.");
            return;
        }

        JFrame frame = new JFrame("Music Player");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(600, 400);
        frame.setLayout(null);

        JButton playButton = new JButton("Play");
        playButton.setBounds(30, 20, 80, 30);
        playButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                play();
            }
        });
        playButton.setBackground(Color.green);

        JButton stopButton = new JButton("Stop");
        stopButton.setBounds(120, 20, 80, 30);
        stopButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                stop();
            }
        });
        stopButton.setBackground(Color.GREEN);

        JButton resumeButton = new JButton("Resume");
        resumeButton.setBounds(30, 60, 80, 30);
        resumeButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                resume();
            }
        });
        resumeButton.setBackground(Color.GREEN);

        JButton restartButton = new JButton("Restart");
        restartButton.setBounds(120, 60, 80, 30);
        restartButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                restart();
            }
        });
        restartButton.setBackground(Color.green);

        JButton nextButton = new JButton("Next");
        nextButton.setBounds(210, 20, 80, 30);
        nextButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                next();
            }
        });
        nextButton.setBackground(Color.green);

        JButton prevButton = new JButton("Previous");
        prevButton.setBounds(210, 60, 80, 30);
        prevButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                previous();
            }
        });
        prevButton.setBackground(Color.GREEN);

        JButton skipForwardButton = new JButton("Skip +10s");
        skipForwardButton.setBounds(300, 20, 120, 30);
        skipForwardButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                skipForward();
            }
        });
        skipForwardButton.setBackground(Color.white);

        JButton skipBackwardButton = new JButton("Back -10s");
        skipBackwardButton.setBounds(300, 60, 120, 30);
        skipBackwardButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                skipBackward();
            }
        });
        skipBackwardButton.setBackground(Color.white);

        songLabel = new JLabel("Song: ");
        songLabel.setBounds(30, 100, 300, 30);
        songLabel.setForeground(Color.WHITE);

        currentDurationLabel = new JLabel("Duration: 0:00");
        currentDurationLabel.setBounds(30, 120, 100, 30);
        currentDurationLabel.setForeground(Color.WHITE);
        currentDurationLabel.setFont(currentDurationLabel.getFont().deriveFont(Font.BOLD));

        songSlider = new JSlider();
        songSlider.setBounds(30, 150, 300, 20);
        songSlider.setMinimum(0);
        songSlider.setValue(0);

        frame.add(playButton);
        frame.add(stopButton);
        frame.add(resumeButton);
        frame.add(restartButton);
        frame.add(nextButton);
        frame.add(prevButton);
        frame.add(skipForwardButton);
        frame.add(skipBackwardButton);
        frame.add(songLabel);
        frame.add(currentDurationLabel);
        frame.add(songSlider);

        timer = new Timer(1000, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                updateSongDuration();
            }
        });
        timer.start();

        frame.setVisible(true);
    }

    private static void resizeBackgroundImage(JFrame frame, JLabel backgroundLabel) {
        int width = frame.getWidth();
        int height = frame.getHeight();

        Image img = ((ImageIcon) backgroundLabel.getIcon()).getImage();
        Image scaledImg = img.getScaledInstance(width, height, Image.SCALE_SMOOTH);
        ImageIcon resizedImage = new ImageIcon(scaledImg);
        backgroundLabel.setIcon(resizedImage);
        backgroundLabel.setBounds(0, 0, width, height);
    }

    public static void mergeSort(String arr[], int si, int ei) {
        if (si < ei) {
            int mid = si + (ei - si) / 2;
            mergeSort(arr, si, mid);
            mergeSort(arr, mid + 1, ei);
            merge(arr, si, mid, ei);
        }
    }

    public static void merge(String arr[], int si, int mid, int ei) {
        int n1 = mid - si + 1;
        int n2 = ei - mid;

        String[] left = new String[n1];
        String[] right = new String[n2];

        for (int i = 0; i < n1; ++i)
            left[i] = arr[si + i];
        for (int j = 0; j < n2; ++j)
            right[j] = arr[mid + 1 + j];

        int i = 0, j = 0;
        int k = si;
        while (i < n1 && j < n2) {
            if (left[i].compareTo(right[j]) <= 0) {
                arr[k] = left[i];
                i++;
            } else {
                arr[k] = right[j];
                j++;
            }
            k++;
        }

        while (i < n1) {
            arr[k] = left[i];
            i++;
            k++;
        }

        while (j < n2) {
            arr[k] = right[j];
            j++;
            k++;
        }
    }

    private static void play() {
        try {
            if (playlist.isEmpty()) {
                System.out.println("Playlist is empty.");
                return;
            }

            if (clip != null && clip.isRunning()) {
                clip.stop();
            }

            AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(new File(playlist.get(currentIndex)));
            clip = AudioSystem.getClip();

            clip.open(audioInputStream);

            // Extracting the song name from the full path
            String songPath = playlist.get(currentIndex);
            String songName = new File(songPath).getName();
            songLabel.setText("Song: " + songName);

            clip.start();

            updateSongDuration();
            songSlider.setMaximum((int) clip.getMicrosecondLength() / 1000000);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void updateSongDuration() {
        if (clip != null) {
            new Thread(() -> {
                while (clip.isRunning()) {
                    long microsecondPosition = clip.getMicrosecondPosition();
                    int seconds = (int) (microsecondPosition / 1000000) % 60;
                    int minutes = (int) ((microsecondPosition / (1000000 * 60)) % 60);

                    SwingUtilities.invokeLater(() -> {
                        currentDurationLabel.setText(String.format("Duration: %d:%02d", minutes, seconds));
                        songSlider.setValue((int) (microsecondPosition / 1000000));
                    });

                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }).start();
        } else {
            timer.stop();
        }
    }

    private static void stop() {
        if (clip != null && clip.isRunning()) {
            clip.stop();
        }
    }

    private static void resume() {
        if (clip != null && !clip.isRunning()) {
            clip.start(); // Start the clip
            updateSongDuration(); // Continue updating the UI components
        }
    }

    private static void restart() {
        if (clip != null) {
            clip.stop(); // Stop the clip
            clip.setMicrosecondPosition(0); // Set microsecond position to 0
            updateSongDuration(); // Update the UI components
            clip.start(); // Start the clip again
        }
    }

    private static void next() {
        if (!playlist.isEmpty()) {
            currentIndex = (currentIndex + 1) % playlist.size();
            play();
        }
    }

    private static void previous() {
        if (!playlist.isEmpty()) {
            currentIndex = (currentIndex - 1 + playlist.size()) % playlist.size();
            play();
        }
    }

    private static void skipForward() {
        if (clip != null && clip.isRunning()) {
            long currentPosition = clip.getMicrosecondPosition();
            clip.setMicrosecondPosition(currentPosition + 10 * 1_000_000);
            updateSongDuration();
        }
    }

    private static void skipBackward() {
        if (clip != null && clip.isRunning()) {
            long currentPosition = clip.getMicrosecondPosition();
            long newPosition = Math.max(0, currentPosition - 10 * 1_000_000);
            clip.setMicrosecondPosition(newPosition);
            updateSongDuration();
        }
    }
}
