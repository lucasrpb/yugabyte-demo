package yugabyte

import java.util.concurrent.{Executor, ThreadLocalRandom}

import com.datastax.driver.core.{BatchStatement, Cluster, ResultSet, Session}
import com.google.common.util.concurrent.{FutureCallback, Futures, ListenableFuture}
import org.scalatest.FlatSpec

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration._
import scala.compat.java8.FutureConverters._
import scala.language.implicitConversions
import scala.concurrent.{Await, Future, Promise}

class MainSpec extends FlatSpec {

  def read(id: String)(implicit session: Session): Future[Float] = {
    val p = Promise[Float]()

    val ps = session.prepare(s"select balance from accounts where id=?;")
      .bind().setString(0, id)

    Futures.addCallback(session.executeAsync(ps), new FutureCallback[ResultSet] {
      override def onSuccess(result: ResultSet): Unit = {
        p.success(result.one().getFloat("balance"))
      }
      
      override def onFailure(t: Throwable): Unit = {
        p.failure(t)
      }
    }, global.asInstanceOf[Executor])

    p.future
  }

  def write(id: String, value: Float)(implicit session: Session): Future[Boolean] = {
    val p = Promise[Boolean]()

    val ps = session.prepare("update accounts set balance = ? where id = ? if exists;")
      .bind()
      .setFloat(0, value)
      .setString(1, id)

    Futures.addCallback(session.executeAsync(ps), new FutureCallback[ResultSet] {
      override def onSuccess(result: ResultSet): Unit = {
        p.success(result.wasApplied())
      }

      override def onFailure(t: Throwable): Unit = {
        p.failure(t)
      }
    }, global.asInstanceOf[Executor])

    p.future
  }

  "money " should "be the same after transactions" in {

    val rand = ThreadLocalRandom.current()

    val cluster = Cluster.builder()
      .addContactPoints({"127.0.0.1"}).withPort(9042)
      .build()

    implicit val session = cluster.connect("banking")

    val n = 100

    /*val batch = new BatchStatement()

    for(i<-0 until n){
      val id = i.toString()
      val balance = rand.nextInt(0, 1000).toFloat

      val s = session.prepare(s"INSERT INTO accounts(id, balance) VALUES(?,?)")
        .bind()
        .setString("id", id)
        .setFloat("balance", balance)

      batch.add(s)
    }

    println(session.execute(batch))*/

    var tasks = Seq.empty[Future[Boolean]]

    def transfer(): Future[Boolean] = {
      val rand = ThreadLocalRandom.current()

      val i0 = rand.nextInt(0, n)
      val i1 = rand.nextInt(0, n)


    }

    for(i<-0 until 1){
      //tasks = tasks :+ transfer()
    }

    val f = write("x", 1000F).map { r =>
      println(s"result ${r}")
    }

    Await.result(f, 10 seconds)

    session.close()
    cluster.close()
  }

}
