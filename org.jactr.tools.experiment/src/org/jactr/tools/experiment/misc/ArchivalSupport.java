package org.jactr.tools.experiment.misc;

/*
 * default logging
 */
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.CopyOption;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.FileVisitOption;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.StandardCopyOption;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.EnumSet;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class ArchivalSupport
{
  /**
   * Logger definition
   */
  static private final transient Log LOGGER = LogFactory
      .getLog(ArchivalSupport.class);

  static public boolean delete(Path targetDirectory)
  {
    try
    {
      if (!Files.isDirectory(targetDirectory))
        throw new IOException("target is not a directory");

      Files.walkFileTree(targetDirectory,
          EnumSet.of(FileVisitOption.FOLLOW_LINKS), Integer.MAX_VALUE,
          new DeleteDirectory());

      return true;
    }
    catch (IOException ex)
    {
      LOGGER.error("Failed to move data ", ex);
      return false;
    }
  }

  static public boolean move(Path sourceDirectory, Path targetDirectory,
      boolean overWrite)
  {
    try
    {
      if (overWrite)
        Files.move(sourceDirectory, targetDirectory,
            StandardCopyOption.REPLACE_EXISTING);
      else
        Files.move(sourceDirectory, targetDirectory);
      
      return true;
    }
    catch (IOException e)
    {
      // TODO Auto-generated catch block
      LOGGER.error("ArchivalSupport.move threw IOException : ", e);
      return false;
    }
  }

  static public boolean copy(Path sourceDirectory, Path targetDirectory,
      boolean overWrite)
  {
    try
    {
      if (!Files.isDirectory(sourceDirectory))
        throw new IOException("Source is not a directory");

      if (!Files.isDirectory(targetDirectory))
        throw new IOException("target is not a directory");

      Files.walkFileTree(sourceDirectory,
          EnumSet.of(FileVisitOption.FOLLOW_LINKS), Integer.MAX_VALUE,
          new CopyDirectory(sourceDirectory, targetDirectory, overWrite));

      return true;
    }
    catch (IOException ex)
    {
      LOGGER.error("Failed to move data ", ex);
      return false;
    }
  }

  static public boolean archive(Path directory, Path zipFilePath)
  {
    try
    {
      File fp = zipFilePath.toFile();
      fp.getParentFile().mkdirs();
      if (LOGGER.isDebugEnabled())
        LOGGER.debug(String.format("Creating %s", fp));

      ZipOutputStream zos = new ZipOutputStream(new FileOutputStream(fp));

      Files.walkFileTree(directory, EnumSet.of(FileVisitOption.FOLLOW_LINKS),
          Integer.MAX_VALUE, new ArchiveDirectory(directory, zos));

      zos.finish();
      zos.flush();
      zos.close();

      if (LOGGER.isDebugEnabled())
        LOGGER.debug(String.format("Created %s", fp));

      return true;
    }
    catch (Exception e)
    {
      LOGGER.error("Failed to archive ", e);
      return false;
    }
  }

  static private class ArchiveDirectory extends SimpleFileVisitor<Path>
  {
    private Path            _root;

    private ZipOutputStream _zos;

    private byte[]          _buffer = new byte[4096];

    public ArchiveDirectory(Path root, ZipOutputStream zf)
    {
      _root = root.getParent();
      this._zos = zf;
    }

    @Override
    public FileVisitResult visitFile(Path file, BasicFileAttributes attributes)
        throws IOException
    {
      try
      {
        Path relativePath = _root.relativize(file);

        if (LOGGER.isDebugEnabled())
          LOGGER.debug(String.format("New zip entry %s", relativePath));

        _zos.putNextEntry(new ZipEntry(relativePath.toString()));

        FileInputStream fis = new FileInputStream(file.toFile());
        int len = 0;
        int wrote = 0;
        while ((len = fis.available()) > 0)
        {
          int readLength = Math.min(_buffer.length, len);
          fis.read(_buffer, 0, readLength);
          _zos.write(_buffer, 0, readLength);
          wrote += readLength;
        }
        fis.close();

        _zos.closeEntry();

        if (LOGGER.isDebugEnabled())
          LOGGER.debug(String.format("Closed zip entry %s. Wrote %d bytes",
              relativePath, wrote));

        return FileVisitResult.CONTINUE;
      }
      catch (Exception e)
      {
        LOGGER.error(String.format("Failed to archive %s", file), e);
        return FileVisitResult.TERMINATE;
      }
    }

    @Override
    public FileVisitResult preVisitDirectory(Path directory,
        BasicFileAttributes attributes) throws IOException
    {
      try
      {
        Path relativePath = _root.relativize(directory);
        if (LOGGER.isDebugEnabled())
          LOGGER.debug(String.format("New directory entry [%s]", relativePath));

        ZipEntry dir = new ZipEntry(relativePath.toString() + "/");
        _zos.putNextEntry(dir);
        _zos.closeEntry();
        return FileVisitResult.CONTINUE;
      }
      catch (Exception e)
      {
        LOGGER.error(String.format("Failed to archive %s", directory), e);
        return FileVisitResult.TERMINATE;
      }
    }

    @Override
    public FileVisitResult postVisitDirectory(Path directory, IOException ie)
        throws IOException
    {
      try
      {
        return FileVisitResult.CONTINUE;
      }
      catch (Exception e)
      {
        LOGGER.error(String.format("Failed to archive %s", directory), e);
        return FileVisitResult.TERMINATE;
      }
    }
  }

  static private class CopyDirectory extends SimpleFileVisitor<Path>
  {

    private Path    source;

    private Path    target;

    private boolean _overWrite;

    public CopyDirectory(Path source, Path target, boolean overWrite)
    {
      this.source = source;
      this.target = target;
      _overWrite = overWrite;
    }

    @Override
    public FileVisitResult visitFile(Path file, BasicFileAttributes attributes)
        throws IOException
    {
      if (_overWrite)
        Files.copy(file, target.resolve(source.relativize(file)),
            StandardCopyOption.REPLACE_EXISTING);
      else
        Files.copy(file, target.resolve(source.relativize(file)));

      return FileVisitResult.CONTINUE;
    }

    @Override
    public FileVisitResult preVisitDirectory(Path directory,
        BasicFileAttributes attributes) throws IOException
    {
      Path targetDirectory = target.resolve(source.relativize(directory));
      try
      {
        Files.copy(directory, targetDirectory);
      }
      catch (FileAlreadyExistsException e)
      {
        if (!Files.isDirectory(targetDirectory))
        {
          throw e;
        }
      }
      return FileVisitResult.CONTINUE;
    }
  }

  static private class DeleteDirectory extends SimpleFileVisitor<Path>
  {

    public DeleteDirectory()
    {
    }

    @Override
    public FileVisitResult visitFile(Path file, BasicFileAttributes attributes)
        throws IOException
    {
      Files.delete(file);
      return FileVisitResult.CONTINUE;
    }

    @Override
    public FileVisitResult postVisitDirectory(Path dir, IOException exc)
        throws IOException
    {
      Files.delete(dir);
      return FileVisitResult.CONTINUE;
    }
  }
}
