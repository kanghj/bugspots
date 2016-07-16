import bugspots.scan.Walk
import org.eclipse.jgit.revwalk.DepthWalk.Commit
import org.scalatest.FunSuite
import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner

/**
  *
  */
@RunWith(classOf[JUnitRunner])
class WalkTest extends FunSuite {
  test("regex works minimally") {
    assert(new Walk(Nil).isBugFix("Fixes bug") === true)
    assert(new Walk(Nil).isBugFix("Closed thing") === true)
  }

  test("regex can return false") {
    assert(new Walk(Nil).isBugFix("no bug here") === false)
  }

  test("regex accounts for boundary") {
    assert(new Walk(Nil).isBugFix("nofix here") === false)
  }

  test("regex works even when keywords are in the middle") {
    assert(new Walk(Nil).isBugFix("we have a fix here") === true)
  }

  test("regex can handle case") {
    assert(new Walk(Nil).isBugFix("this ClOSEs that") === true)
  }

  // TODO test actual walk
}
